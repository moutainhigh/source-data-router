package com.globalegrow.dy.bts.kafka;

import com.globalegrow.dy.bts.model.*;
import com.globalegrow.util.*;
import com.globalegrow.util.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zaful 推荐报表订单相关字段
 */
@Component
public class ZafulDopamineOrderCounter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String orderPrefix = "dy_zaful_dopamine_order_";

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    protected Configuration configuration;

    private Connection connection;

    @PostConstruct
    public void init() throws IOException {
        kafkaTemplate = new KafkaTemplate<>(producerFactory());
        connection = ConnectionFactory.createConnection(this.configuration);
    }

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        // kafka.metadata.broker.list=10.16.0.214:9092,10.16.0.215:9092,10.16.0.216:9092
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "bts-datanode08:9092");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.ACKS_CONFIG,"-1");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 4096);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 40960);
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,5048576);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    /** 获取工厂 */
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * 处理 kafka 中的 binlog 事件
     *
     * @param record
     */
    @KafkaListener(topics = {"dy_zaful_mysql_binlog"}, groupId = "ZafulDopamineOrderCounter")
    public void listen(ConsumerRecord<String, String> record) {
        String mysqlBinLog = record.value();
        this.logger.debug("mysql event: {}", mysqlBinLog);
        try {
            Map<String, Object> dataMap = GsonUtil.readValue(mysqlBinLog, Map.class);

            String eventType = String.valueOf(dataMap.get("type"));
            String table = String.valueOf(dataMap.get("table"));
            this.logger.debug("mysql event log");
            if ("insert".equals(eventType)) {// 插入事件，取订单商品表、订单表
                this.logger.debug("insert event");
                if ("eload_order_goods".equals(table.toLowerCase())) {
                    this.logger.info("订单商品新增事件");
                    this.handleGoodInfo(dataMap);
                } else if ("eload_order_info".equals(table.toLowerCase())) {
                    this.logger.info("订单新增事件");
                    this.handleOrderInfo(dataMap);
                }
            } else if ("update".equals(eventType)) {// 更新事件，取订单、商品表
                this.logger.debug("insert event");
                if ("eload_order_goods".equals(table.toLowerCase())) {
                    this.logger.info("订单商品修改事件");
                    this.handleGoodInfo(dataMap);
                } else if ("eload_order_info".equals(table.toLowerCase())) {
                    this.logger.info("订单修改事件");
                    this.handleOrderInfo(dataMap);
                }
            }


            this.logger.debug("log handle end!");

        } catch (Exception e) {
            this.logger.error("mysql binlog 处理异常 log：{}", mysqlBinLog, e);
        }

    }

    /**
     * 处理订单商品事件
     *
     * @param dataMap
     * @throws Exception
     */
    private void handleGoodInfo(Map<String, Object> dataMap) throws Exception {
        OrderGoodInfo orderGoodInfo = this.orderGoodInfo(dataMap);
        String redisOrderInfoStr = SpringRedisUtil.getStringValue(this.orderPrefix + orderGoodInfo.getOrderId());
        if (StringUtils.isNotEmpty(redisOrderInfoStr)) {
            this.logger.info("goods info redis 中查询到订单:{}", orderGoodInfo.getOrderId(), this.getEventData(dataMap).get("addtime"));
            RedisOrderInfo redisOrderInfo = GsonUtil.readValue(redisOrderInfoStr, RedisOrderInfo.class);
            redisOrderInfo.getGoodInfoMap().put(orderGoodInfo.getSku(), orderGoodInfo);
            SpringRedisUtil.put(this.orderPrefix + orderGoodInfo.getOrderId(), GsonUtil.toJson(redisOrderInfo), 604800);
            String redisKey = "dy_zaful_app_dopamine_" + redisOrderInfo.getOrderInfo().getUserId() + "_" + orderGoodInfo.getSku();
            String cache = SpringRedisUtil.getStringValue(redisKey);
            handleReportQuota(redisOrderInfo, orderGoodInfo, cache);

        } else {
            this.logger.info("goods info 无订单信息，根据订单商品新建空订单信息: ", orderGoodInfo.getOrderId(), this.getEventData(dataMap).get("addtime"));
            RedisOrderInfo redisOrderInfo = new RedisOrderInfo();
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderId(orderGoodInfo.getOrderId());
            redisOrderInfo.setOrderInfo(orderInfo);
            redisOrderInfo.getGoodInfoMap().put(orderGoodInfo.getSku(), orderGoodInfo);
            SpringRedisUtil.put(this.orderPrefix + orderGoodInfo.getOrderId(), GsonUtil.toJson(redisOrderInfo), 604800);

        }
    }

    /**
     * 处理订单事件
     *
     * @param dataMap
     * @throws Exception
     */
    private void handleOrderInfo(Map<String, Object> dataMap) throws Exception {
        OrderInfo orderInfo = this.orderInfo(dataMap);
        String redisOrderInfoStr = SpringRedisUtil.getStringValue(this.orderPrefix + orderInfo.getOrderId());
        if (StringUtils.isNotEmpty(redisOrderInfoStr)) {
            this.logger.info("order info redis 中查询到订单: {},time: {}", orderInfo.getOrderId(), this.getEventData(dataMap).get("add_time"));
            RedisOrderInfo redisOrderInfo = GsonUtil.readValue(redisOrderInfoStr, RedisOrderInfo.class);
            redisOrderInfo.setOrderInfo(orderInfo);
            if (redisOrderInfo.getGoodInfoMap().size() > 0) {
                this.logger.info("order info 更新 redis 中的 adt 埋点订单信息");
                for (Map.Entry<String, OrderGoodInfo> entry : redisOrderInfo.getGoodInfoMap().entrySet()) {
                    // 处理sku 下单数、销售额、下单商品数、销量、整体指标
                    OrderGoodInfo orderGoodInfo = entry.getValue();
                    redisOrderInfo.getGoodInfoMap().put(orderGoodInfo.getSku(), orderGoodInfo);
                    SpringRedisUtil.put(this.orderPrefix + orderGoodInfo.getOrderId(), GsonUtil.toJson(redisOrderInfo), 604800);
                    String redisKey = "dy_zaful_app_dopamine_" + redisOrderInfo.getOrderInfo().getUserId() + "_" + orderGoodInfo.getSku();
                    String cache = SpringRedisUtil.getStringValue(redisKey);
                    handleReportQuota(redisOrderInfo, orderGoodInfo, cache);


                }
            }

        } else {
            this.logger.info("redis 中无订单信息，新建空订单: {}, time: {}", orderInfo.getOrderId(), this.getEventData(dataMap).get("add_time"));
            RedisOrderInfo redisOrderInfo = new RedisOrderInfo();
            redisOrderInfo.setOrderInfo(orderInfo);
            SpringRedisUtil.put(this.orderPrefix + orderInfo.getOrderId(), GsonUtil.toJson(redisOrderInfo), 604800);

        }

    }

    private void handleReportQuota(RedisOrderInfo redisOrderInfo, OrderGoodInfo orderGoodInfo, String cache) {
        if (StringUtils.isNotEmpty(cache)) {
            SkuCartInfo skuCartInfo = GsonUtil.readValue(cache, SkuCartInfo.class);
            // 处理下单数、下单用户数、已支付订单数、GMV、整体指标
            List<Map<String, String>> btsList = this.getBtsInfoFromHbase(skuCartInfo.getDeviceId());
            if (btsList != null && btsList.size() > 0) {
                btsList.forEach(bts -> {
                    BtsAppDopamineReportQuota quota = new BtsAppDopamineReportQuota();
                    quota.setBts(bts);
                    String orderStatus = redisOrderInfo.getOrderInfo().getOrderStatus();
                    if (skuCartInfo.getRecommend()) {
                        quota.setOrder(Integer.valueOf(redisOrderInfo.getOrderInfo().getOrderId()));
                        quota.setOrder_uv(skuCartInfo.getDeviceId());
                        if ("0".equals(orderStatus)) {
                            quota.setOrder_sku(1);
                            quota.setOrder_uv(skuCartInfo.getDeviceId());
                            quota.setOrder_amount(orderGoodInfo.getAmount());
                        }
                        if ("1".equals(orderStatus) || "8".equals(orderStatus)) {
                            quota.setPaid_order(Integer.valueOf(redisOrderInfo.getOrderInfo().getOrderId()));
                            quota.setPaid_uv(skuCartInfo.getDeviceId());
                            quota.setAmount(orderGoodInfo.getAmount());
                            quota.setSales_amount(orderGoodInfo.getGoodsNum());
                        }

                    }else {
                        if ("0".equals(orderStatus)) {
                            quota.setWhole_order_uv(skuCartInfo.getDeviceId());
                            quota.setWhole_order_amount(orderGoodInfo.getAmount());
                        }
                        if ("1".equals(orderStatus) || "8".equals(orderStatus)) {
                            quota.setWhole_amount(orderGoodInfo.getAmount());
                            quota.setWhole_paid_uv(skuCartInfo.getDeviceId());
                        }
                    }

                    Map reportMap = DyBeanUtils.objToMap(quota);
                    reportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
                    this.kafkaTemplate.send("dy_bts_dopamine_report", GsonUtil.toJson(reportMap));
                });
            }

        }
    }

    private List<Map<String, String>> getBtsInfoFromHbase(String deviceId) {
        try (Table table = connection.getTable(TableName.valueOf("dy_cookie_bts_info_rel"));) {
            Scan s = new Scan();
            s.setFilter(new PrefixFilter(deviceId.getBytes()));
            ResultScanner rs = table.getScanner(s);
            if (rs != null) {
                List<Map<String, String>> list = new ArrayList<>();
                for (Result result : rs) {
                    String value = Bytes.toString(result.getValue(Bytes.toBytes("bts_info"), Bytes.toBytes("info")));
                    if (StringUtils.isNotEmpty(value) && !"null".equals(value)) {
                        list.add(GsonUtil.readValue(value, Map.class));
                    }

                }
                return list;
            }
        } catch (IOException e) {
            this.logger.error("从 hbase 查询 bts 信息失败", e);
        }
        return null;
    }

    private OrderGoodInfo orderGoodInfo(Map<String, Object> dataMap) {
        Map<String, Object> tableData = this.getEventData(dataMap);
        OrderGoodInfo orderGoodInfo = new OrderGoodInfo();
        String orderId = String.valueOf(tableData.get(OrderGoodInfo.ORDER_ID));
        if (orderId.contains(".")) {
            orderId = orderId.substring(0, orderId.lastIndexOf("."));
        }
        orderGoodInfo.setOrderId(orderId);
        orderGoodInfo.setGoodsNum(NUmberUtils.jsonValueToInt((String.valueOf(tableData.get(OrderGoodInfo.GOODS_NUM)))));
        orderGoodInfo.setSku(String.valueOf(tableData.get(OrderGoodInfo.SKU)));
        orderGoodInfo.setPrice(Float.valueOf(String.valueOf(tableData.get(OrderGoodInfo.PRICE))));
        Float f = Float.valueOf(String.valueOf(tableData.get("goods_pay_amount"))) * 100;
        orderGoodInfo.setAmount(f.intValue());
        return orderGoodInfo;
    }

    private OrderInfo orderInfo(Map<String, Object> dataMap) {
        Map<String, Object> tableData = this.getEventData(dataMap);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(String.valueOf(tableData.get(OrderInfo.USER_ID)));
        orderInfo.setOrderId(String.valueOf(tableData.get(OrderInfo.ORDER_ID)));
        orderInfo.setOrderStatus(String.valueOf(tableData.get(OrderInfo.ORDER_STATUS)));
        return orderInfo;
    }

    private Map<String, Object> getEventData(Map<String, Object> dataMap) {
        return (Map<String, Object>) dataMap.get("data");
    }


}
