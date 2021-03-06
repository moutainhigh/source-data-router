package com.globalegrow.report.order;


import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * zaful bin log 埋点处理
 */
@Component
public class ZafulOrderInfoHandle {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ZAFUL_REPORT_ORDER_INFO_REDIS_PREFIX = "DY_RO_ZAFUL_";

    private static final String ZAFUL_ORDER = "ZAFUL_ORDER";

    @Autowired
    @Qualifier("executorServiceMap")
    private Map<String, ExecutorService> executorServiceMap;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.order.zaful.seconds:1209600}")
    private Long orderCacheSeconds;


    @KafkaListener(topics = {"dy_zaful_mysql_binlog"}, groupId = "${dy_bts_zaful_report_order}")
    public void listen(ConsumerRecord<String, String> record) {
        String mysqlBinLog = record.value();
        //this.logger.debug("mysql event: {}", mysqlBinLog);
        try {
            Map<String, Object> dataMap = JacksonUtil.readValue(mysqlBinLog, Map.class);
            String eventType = String.valueOf(dataMap.get("type"));
            String table = String.valueOf(dataMap.get("table"));

            if ("eload_order_info".equals(table.toLowerCase())) {
                this.logger.debug("订单表事件");

                Map<String, Object> tableData = this.getEventData(dataMap);
                OrderInfo orderInfo = this.orderInfo(tableData);
                List<ReportOrderInfo> reportOrderInfos = this.getByOrder(orderInfo);

                long goodsNum = reportOrderInfos.stream().filter(reportOrderInfo -> !reportOrderInfo.getOrder_data()).count();

                if (goodsNum > 0) {

                    this.logger.debug("包含订单商品事件，像 kafka 发送消息");
                    this.orderInfo(reportOrderInfos, orderInfo.getOrderId());

                } else {
                    this.cacheOrderDataToRedis(reportOrderInfos, orderInfo.getOrderId());
                }


            } else if ("eload_order_goods".equals(table.toLowerCase())) {

                this.logger.debug("订单商品事件");

                if ("insert".equals(eventType)) {
                    Map<String, Object> tableData = this.getEventData(dataMap);
                    OrderGoodInfo goodInfo = this.orderGoodInfo(tableData);
                    List<ReportOrderInfo> reportOrderInfos = this.getByOrderGoods(goodInfo);

                    // 判断是否有订单数据
                    long orderSize = reportOrderInfos.stream().filter(reportOrderInfo -> reportOrderInfo.getOrder_data()).count();

                    if (orderSize > 0) {

                        this.logger.debug("包含订单事件，像 kafka 发送消息");
                        this.orderInfo(reportOrderInfos, goodInfo.getOrderId());

                    } else {
                        this.cacheOrderDataToRedis(reportOrderInfos, goodInfo.getOrderId());
                    }


                }
            }

        } catch (Exception e) {
            logger.error("订单解析处理出错", e);
        }
    }

    /**
     * 订单数据缓存至 redis
     * @param reportOrderInfos
     * @param orderId
     */
    private void cacheOrderDataToRedis(List<ReportOrderInfo> reportOrderInfos, String orderId) {
        GbOrderInfoHandle.cacheOrderAndOrderGoodsToRedis(reportOrderInfos, orderId, ZAFUL_REPORT_ORDER_INFO_REDIS_PREFIX, this.logger, this.orderCacheSeconds);

    }

    /**
     * 处理订单数据，将找到埋点数据的订单发送到 kafka topic
     * 订单数据 topic dy_log_cart_order_info
     *
     * @param reportOrderInfos
     */
    private void orderInfo(List<ReportOrderInfo> reportOrderInfos, String orderId) {

        List<ReportOrderInfo> updateToRedis = new ArrayList<>();
        // 补全订单状态、用户id等数据
        List<ReportOrderInfo> orders = reportOrderInfos.stream().filter(reportOrderInfo -> reportOrderInfo.getOrder_data() && !reportOrderInfo.getHas_sent()).collect(Collectors.toList());
        updateToRedis.addAll(orders);
        if (orders.size() > 0) {
            ReportOrderInfo orderInfo = orders.get(0);
            String userId = orderInfo.getUser_id();
            String orderStatus = orderInfo.getOrder_status();

            List<ReportOrderInfo> goodsInfo = reportOrderInfos.stream().filter(reportOrderInfo -> !reportOrderInfo.getHas_sent() && !reportOrderInfo.getOrder_data()).collect(Collectors.toList());


            // 订单商品，有新增时发送一次，每次有订单状态更新时全部重新发送一次
            reportOrderInfos.stream().filter(reportOrderInfo -> !reportOrderInfo.getOrder_data()).collect(Collectors.toList()).stream().forEach(reportOrderInfo -> {
                reportOrderInfo.setOrder_status(orderStatus);
                reportOrderInfo.setUser_id(userId);
                logger.debug("根据当前运行报表查询 redis 中的加购埋点数据:{}", this.executorServiceMap.keySet());
                //循环所有报表 根据 用户 sku 查找埋点
                executorServiceMap.keySet().stream().filter(key -> key.contains(ZAFUL_ORDER)).forEach(key -> {
                    ReportOrderInfo reportOrderInfo1 = new ReportOrderInfo();
                    BeanUtils.copyProperties(reportOrderInfo, reportOrderInfo1);
                    reportOrderInfo1.setReport_name(key);
                    String cartKey = key + "_" + userId + "_" + reportOrderInfo1.getSku();
                    String cartLog = SpringRedisUtil.getStringValue(cartKey);
                    this.logger.debug("根据当前运行报表查询到 redis key:{} 数据:{}", cartKey, cartLog);
                    GbOrderInfoHandle.sendOrderBurryToOrderTopic(reportOrderInfo1, cartLog, this.logger, this.kafkaTemplate);

                });


            });

            updateToRedis.addAll(goodsInfo);

        }

        updateToRedis.forEach(reportOrderInfo -> reportOrderInfo.setHas_sent(true));

        this.cacheOrderDataToRedis(updateToRedis, orderId);

    }

    private List<ReportOrderInfo> getByOrder(OrderInfo orderInfo) {
        List<ReportOrderInfo> reportOrderInfos = new ArrayList<>();

        ReportOrderInfo reportOrderInfo = new ReportOrderInfo();
        reportOrderInfo.setOrder_id(orderInfo.getOrderId());
        reportOrderInfo.setOrder_status(orderInfo.getOrderStatus());
        reportOrderInfo.setUser_id(orderInfo.getUserId());
        reportOrderInfo.setOrder_data(true);
        reportOrderInfos.add(reportOrderInfo);

        Set<String> strings = this.orderCache(orderInfo.getOrderId());
        if (strings != null && strings.size() > 0) {

            strings.stream().forEach(s -> {
                try {
                    reportOrderInfos.add(JacksonUtil.readValue(s, ReportOrderInfo.class));
                } catch (Exception e) {

                }
            });
        }

        SpringRedisUtil.del(ZAFUL_REPORT_ORDER_INFO_REDIS_PREFIX + orderInfo.getOrderId());
        return reportOrderInfos;
    }

    private List<ReportOrderInfo> getByOrderGoods(OrderGoodInfo orderGoodInfo) {
        List<ReportOrderInfo> reportOrderInfos = new ArrayList<>();

        ReportOrderInfo reportOrderInfo = new ReportOrderInfo();
        reportOrderInfo.setOrder_id(orderGoodInfo.getOrderId());
        reportOrderInfo.setSku(orderGoodInfo.getSku());
        reportOrderInfo.setGmv(orderGoodInfo.getGmv());
        reportOrderInfo.setAmount_num_price(orderGoodInfo.getAmount());
        reportOrderInfo.setGoods_num(orderGoodInfo.getGoodsNum());

        reportOrderInfos.add(reportOrderInfo);

        Set<String> strings = this.orderCache(orderGoodInfo.getOrderId());
        if (strings != null && strings.size() > 0) {

            strings.stream().forEach(s -> {
                try {
                    reportOrderInfos.add(JacksonUtil.readValue(s, ReportOrderInfo.class));
                } catch (Exception e) {

                }
            });
        }

        SpringRedisUtil.del(ZAFUL_REPORT_ORDER_INFO_REDIS_PREFIX + orderGoodInfo.getOrderId());
        return reportOrderInfos;
    }


    private Set<String> orderCache(String orderId) {
        return SpringRedisUtil.SMEMBERS(ZAFUL_REPORT_ORDER_INFO_REDIS_PREFIX + orderId);
    }


    private OrderGoodInfo orderGoodInfo(Map<String, Object> tableData) {
        //Map<String, Object> tableData = this.getEventData(dataMap);
        OrderGoodInfo orderGoodInfo = new OrderGoodInfo();
        orderGoodInfo.setOrderId(String.valueOf(tableData.get(OrderGoodInfo.ORDER_ID)));
        orderGoodInfo.setGoodsNum(Integer.valueOf(String.valueOf(tableData.get(OrderGoodInfo.GOODS_NUM))));
        orderGoodInfo.setSku(String.valueOf(tableData.get(OrderGoodInfo.SKU)));
        orderGoodInfo.setPrice(Float.valueOf(String.valueOf(tableData.get(OrderGoodInfo.PRICE))));

        if (tableData.get("goods_pay_amount") == null || "0".equals(String.valueOf(tableData.get("goods_pay_amount"))) ||
        "0.00".equals(String.valueOf(tableData.get("goods_pay_amount")))) {
            orderGoodInfo.setGmv(orderGoodInfo.getAmount());
        }else {
            Float f = (Float.valueOf(String.valueOf(tableData.get("goods_pay_amount"))) * 100);
            if (f.intValue() <= 0) {
                orderGoodInfo.setGmv(orderGoodInfo.getAmount());
            }else {
                orderGoodInfo.setGmv(f.intValue());
            }

        }

        return orderGoodInfo;
    }

    private OrderInfo orderInfo(Map<String, Object> tableData) {
        //Map<String, Object> tableData = this.getEventData(dataMap);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(String.valueOf(tableData.get(OrderInfo.USER_ID)));
        orderInfo.setOrderId(String.valueOf(tableData.get(OrderInfo.ORDER_ID)));
        orderInfo.setOrderStatus(String.valueOf(tableData.get(OrderInfo.ORDER_STATUS)));
        return orderInfo;
    }

    private Map<String, Object> getEventData(Map<String, Object> dataMap) {
        return (Map<String, Object>) dataMap.get("data");
    }

    public Long getOrderCacheSeconds() {
        return orderCacheSeconds;
    }

    public void setOrderCacheSeconds(Long orderCacheSeconds) {
        this.orderCacheSeconds = orderCacheSeconds;
    }
}
