package com.globalegrow.report.order;

import com.globalegrow.report.enums.ReportEnums;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * gb binlog 数据处理
 */
@Component
public class GbOrderInfoHandle {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String GB_REPORT_ORDER_INFO_REDIS_PREFIX = "DY_RO_GB_";

    private static final String GB_ORDER = "GB_ORDER";

    @Value("${app.order.gb.seconds:1209600}")
    private Long orderCacheSeconds;

    @Autowired
    @Qualifier("executorServiceMap")
    private Map<String, ExecutorService> executorServiceMap;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = {"dy_gb_mysql_binlog"})
    public void listen(ConsumerRecord<String, String> record) {
        String mysqlBinLog = record.value();
        try {
            Map<String, Object> dataMap = JacksonUtil.readValue(mysqlBinLog, Map.class);
            String eventType = String.valueOf(dataMap.get("type"));
            String table = String.valueOf(dataMap.get("table"));

            if (StringUtils.isNumeric(String.valueOf(dataMap.get("table")).replace("order_info_", ""))) {
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


            } else if (StringUtils.isNumeric(String.valueOf(dataMap.get("table")).replace("order_goods_", ""))) {

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
        cacheOrderAndOrderGoodsToRedis(reportOrderInfos, orderId, GB_REPORT_ORDER_INFO_REDIS_PREFIX, this.logger, this.orderCacheSeconds);

    }

    static void cacheOrderAndOrderGoodsToRedis(List<ReportOrderInfo> reportOrderInfos, String orderId, String gbReportOrderInfoRedisPrefix, Logger logger, Long cacheSeconds) {
        if (reportOrderInfos.size() > 0) {
            try {
                List<String> list = reportOrderInfos.stream().map(reportOrderInfo -> {
                    try {
                        return JacksonUtil.toJSon(reportOrderInfo);
                    } catch (Exception e) {
                        return "";
                    }
                }).collect(Collectors.toList());

                SpringRedisUtil.putSet(gbReportOrderInfoRedisPrefix + orderId, cacheSeconds, list.toArray(new String[list.size()]));
            } catch (Exception e) {
                logger.error("订单信息缓存至 redis 失败: {}", reportOrderInfos, e);
            }
        }
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
                //reportOrderInfo.setUser_id(userId);
                logger.info("根据当前运行报表查询 redis 中的加购埋点数据:{}", this.executorServiceMap.keySet());
                //循环所有报表 根据 用户 sku 查找埋点
                executorServiceMap.keySet().stream().filter(key -> key.contains(GB_ORDER)).forEach(key -> {
                    String cartKey = key + "_" + userId + "_" + reportOrderInfo.getSku();
                    String cartLog = SpringRedisUtil.getStringValue(cartKey);
                    this.logger.info("根据当前运行报表查询到 redis key:{} 数据:{}", cartKey, cartLog);
                    sendOrderBurryToOrderTopic(reportOrderInfo, cartLog, this.logger, this.kafkaTemplate);

                });


            });

            updateToRedis.addAll(goodsInfo);

        }

        updateToRedis.forEach(reportOrderInfo -> reportOrderInfo.setHas_sent(true));

        this.cacheOrderDataToRedis(updateToRedis, orderId);

    }

    /**
     * 加购与埋点数据关联后发送到 kafka
     * @param reportOrderInfo
     * @param cartLog
     * @param logger
     * @param kafkaTemplate
     */
    static void sendOrderBurryToOrderTopic(ReportOrderInfo reportOrderInfo, String cartLog, Logger logger, KafkaTemplate<String, String> kafkaTemplate) {
        if (StringUtils.isNotEmpty(cartLog)) {
            try {
                Map<String, Object> logMap = JacksonUtil.readValue(cartLog, Map.class);
                logMap.put(ReportEnums.db_order_info.name(), reportOrderInfo);
                String orderData = JacksonUtil.toJSon(logMap);
                logger.info("根据当前运行报表查询到, 订单数据： {}", orderData);
                kafkaTemplate.send("dy_log_cart_order_info",orderData );
            } catch (Exception e) {
                logger.error("发送到 order kafka 失败: {} {}", cartLog, reportOrderInfo, e);
                //e.printStackTrace();
            }
        }
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

        SpringRedisUtil.del(GB_REPORT_ORDER_INFO_REDIS_PREFIX + orderInfo.getOrderId());
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

        SpringRedisUtil.del(GB_REPORT_ORDER_INFO_REDIS_PREFIX + orderGoodInfo.getOrderId());
        return reportOrderInfos;
    }


    private Set<String> orderCache(String orderId) {
        return SpringRedisUtil.SMEMBERS(GB_REPORT_ORDER_INFO_REDIS_PREFIX + orderId);
    }


    private OrderGoodInfo orderGoodInfo(Map<String, Object> tableData) {
        //Map<String, Object> tableData = this.getEventData(dataMap);
        OrderGoodInfo orderGoodInfo = new OrderGoodInfo();
        orderGoodInfo.setOrderId(String.valueOf(tableData.get("order_sn")));
        orderGoodInfo.setUserId(String.valueOf(tableData.get("user_id")));
        orderGoodInfo.setGoodsNum(Integer.valueOf(String.valueOf(tableData.get("qty"))));
        orderGoodInfo.setSku(String.valueOf(tableData.get(OrderGoodInfo.SKU)));
        orderGoodInfo.setPrice(Float.valueOf(String.valueOf(tableData.get("price"))));
        orderGoodInfo.setGmv(orderGoodInfo.getAmount());
        /*if (tableData.get("goods_pay_amount") == null || "0".equals(String.valueOf(tableData.get("goods_pay_amount"))) ||
                "0.00".equals(String.valueOf(tableData.get("goods_pay_amount")))) {
            orderGoodInfo.setGmv(orderGoodInfo.getAmount());
        }else {
            Float f = (Float.valueOf(String.valueOf(tableData.get("goods_pay_amount"))) * 100);
            if (f.intValue() <= 0) {
                orderGoodInfo.setGmv(orderGoodInfo.getAmount());
            }else {
                orderGoodInfo.setGmv(f.intValue());
            }

        }*/

        return orderGoodInfo;
    }

    private OrderInfo orderInfo(Map<String, Object> tableData) {
        //Map<String, Object> tableData = this.getEventData(dataMap);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(String.valueOf(tableData.get(OrderInfo.USER_ID)));
        orderInfo.setOrderId(String.valueOf(tableData.get("order_sn")));
        orderInfo.setOrderStatus(String.valueOf(tableData.get("pay_status")));
        return orderInfo;
    }

    private Map<String, Object> getEventData(Map<String, Object> dataMap) {
        return (Map<String, Object>) dataMap.get("data");
    }

}
