package com.globalegrow.binlog.kafka;

import com.globalegrow.binlog.model.OrderGoodInfo;
import com.globalegrow.binlog.model.OrderInfo;
import com.globalegrow.binlog.model.RedisOrderInfo;
import com.globalegrow.bts.BtsPlanInfoQuery;
import com.globalegrow.bts.RecommendTypeUtil;
import com.globalegrow.bts.model.BtsZafulListPageReport;
import com.globalegrow.bts.model.GoodsAddCartInfo;
import com.globalegrow.dy.report.enums.RecommendQuotaFields;
import com.globalegrow.dy.report.enums.RecommendReportFields;
import com.globalegrow.dy.report.enums.ValueType;
import com.globalegrow.bts.enums.CartRedisKeysPrefix;
import com.globalegrow.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
@ConfigurationProperties(prefix = "app.kafka")
public class MysqlBinlogKafkaCustomer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);

    private final String orderPrefix = "dy_zaful_order_";

    private final String adtZafulPrefix = "dy_zaful_adt_";

    private final String logTimestampKey = "glb_tm";

    private final String logAdtOrderKey = "order";
    private final String logAdtOrderId = "id";
    private final String logAdtOrderStatusKey = "status";
    private final String logAdtOrderAmountKey = "amount";
    private final String btsKey = "bts";
    private final String fmd = "glb_fmd";
    private final String cookieKey = "glb_od";
    private final String productCode = "ZF";
    private final String goodsNumKey = "goodsNum";

    @Value("${app.redis.expire-seconds:86400}")
    private Long expireSeconds = 86400L;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private BtsPlanInfoQuery btsPlanInfoQuery;

    private List<String> topics;

    @Value("${app.kafka.zaful.list-page-report:dy_bts_zaful_list_page_report}")
    private String listPageReportTopic;

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public enum filedKeys {
        glb_mrlc, glb_fmd, glb_plf, glb_b, glb_od, bts
    }

    public enum products {
        ZF
    }

    // {"fmd":"mr_T_3","versionid":"23","bucketid":"5","planid":"9"}
    public enum zafulBts {
        versionid, bucketid, planid
    }

    public enum bts {
        bucket_id, plan_id, version_id
    }

    /**
     * ?????? kafka ?????? binlog ??????
     *
     * @param record
     */
    @KafkaListener(topics = {"${app.kafka.zaful.topic}"})
    public void listen(ConsumerRecord<String, String> record) {
        String mysqlBinLog = record.value();
        this.logger.debug("mysql event: {}", mysqlBinLog);
        try {
            Map<String, Object> dataMap = JacksonUtil.readValue(mysqlBinLog, Map.class);

            String eventType = String.valueOf(dataMap.get("type"));
            String table = String.valueOf(dataMap.get("table"));
            this.logger.debug("mysql event log");
            if ("insert".equals(eventType)) {// ?????????????????????????????????????????????
                this.logger.debug("insert event");
                if ("eload_order_goods".equals(table.toLowerCase())) {
                    this.logger.info("????????????????????????");
                    this.handleGoodInfo(dataMap);
                } else if ("eload_order_info".equals(table.toLowerCase())) {
                    this.logger.info("??????????????????");
                    this.handleOrderInfo(dataMap);
                }
            } else if ("update".equals(eventType)) {// ????????????????????????????????????
                this.logger.debug("insert event");
                if ("eload_order_goods".equals(table.toLowerCase())) {
                    this.logger.info("????????????????????????");
                    this.handleGoodInfo(dataMap);
                } else if ("eload_order_info".equals(table.toLowerCase())) {
                    this.logger.info("??????????????????");
                    this.handleOrderInfo(dataMap);
                }
            }


            this.logger.debug("log handle end!");

        } catch (Exception e) {
            this.logger.error("mysql binlog ???????????? log???{}", mysqlBinLog, e);
        }
        this.countDownLatch1.countDown();
    }

    /**
     * ????????????????????????
     *
     * @param dataMap
     * @throws Exception
     */
    private void handleGoodInfo(Map<String, Object> dataMap) throws Exception {
        OrderGoodInfo orderGoodInfo = this.orderGoodInfo(dataMap);
        String redisOrderInfoStr = SpringRedisUtil.getStringValue(this.orderPrefix + orderGoodInfo.getOrderId());
        if (StringUtils.isNotEmpty(redisOrderInfoStr)) {
            this.logger.info("goods info redis ??????????????????:{}", orderGoodInfo.getOrderId(), this.getEventData(dataMap).get("addtime"));
            RedisOrderInfo redisOrderInfo = JacksonUtil.readValue(redisOrderInfoStr, RedisOrderInfo.class);
            redisOrderInfo.getGoodInfoMap().put(orderGoodInfo.getSku(), orderGoodInfo);
            SpringRedisUtil.put(this.orderPrefix + orderGoodInfo.getOrderId(), JacksonUtil.toJSon(redisOrderInfo), expireSeconds);
            this.logAdtOrderInfo(orderGoodInfo, redisOrderInfo);

        } else {
            this.logger.info("goods info ?????????????????????????????????????????????????????????: ", orderGoodInfo.getOrderId(), this.getEventData(dataMap).get("addtime"));
            RedisOrderInfo redisOrderInfo = new RedisOrderInfo();
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderId(orderGoodInfo.getOrderId());
            redisOrderInfo.setOrderInfo(orderInfo);
            redisOrderInfo.getGoodInfoMap().put(orderGoodInfo.getSku(), orderGoodInfo);
            SpringRedisUtil.put(this.orderPrefix + orderGoodInfo.getOrderId(), JacksonUtil.toJSon(redisOrderInfo), expireSeconds);

        }
    }

    /**
     * ??????????????????
     *
     * @param dataMap
     * @throws Exception
     */
    private void handleOrderInfo(Map<String, Object> dataMap) throws Exception {
        OrderInfo orderInfo = this.orderInfo(dataMap);
        String redisOrderInfoStr = SpringRedisUtil.getStringValue(this.orderPrefix + orderInfo.getOrderId());
        if (StringUtils.isNotEmpty(redisOrderInfoStr)) {
            this.logger.info("order info redis ??????????????????: {},time: {}", orderInfo.getOrderId(), this.getEventData(dataMap).get("add_time"));
            RedisOrderInfo redisOrderInfo = JacksonUtil.readValue(redisOrderInfoStr, RedisOrderInfo.class);
            redisOrderInfo.setOrderInfo(orderInfo);
            if (redisOrderInfo.getGoodInfoMap().size() > 0) {
                this.logger.info("order info ?????? redis ?????? adt ??????????????????");
                for (Map.Entry<String, OrderGoodInfo> entry : redisOrderInfo.getGoodInfoMap().entrySet()) {
                    this.logAdtOrderInfo(entry.getValue(), redisOrderInfo);
                }
            }

        } else {
            this.logger.info("redis ????????????????????????????????????: {}, time: {}", orderInfo.getOrderId(), this.getEventData(dataMap).get("add_time"));
            RedisOrderInfo redisOrderInfo = new RedisOrderInfo();
            redisOrderInfo.setOrderInfo(orderInfo);
            SpringRedisUtil.put(this.orderPrefix + orderInfo.getOrderId(), JacksonUtil.toJSon(redisOrderInfo), expireSeconds);

        }

    }

    /**
     * ?????????????????????????????????????????????????????? kafka
     *
     * @param orderGoodInfo
     * @param redisOrderInfo
     * @throws Exception
     */
    private void logAdtOrderInfo(OrderGoodInfo orderGoodInfo, RedisOrderInfo redisOrderInfo) throws Exception {
        if (StringUtils.isNotEmpty(redisOrderInfo.getOrderInfo().getUserId())) {
            this.logger.info("???????????? redis ??????????????????");
            Map<String, Object> logAdt = this.redisAdtData(orderGoodInfo.getSku(), redisOrderInfo.getOrderInfo().getUserId());
            this.logger.info("?????? sku: {}???userid: {} ?????????,????????????????????????: {}", orderGoodInfo.getSku(), redisOrderInfo.getOrderInfo().getUserId(), logAdt);
            if (logAdt != null) {
                this.logger.info("????????????????????????????????????");
                Map<String, Object> order = this.order(orderGoodInfo, redisOrderInfo);
                logAdt.put(this.logAdtOrderKey, order);
                this.logger.info("?????????????????? kafka topic");
                this.sendLogAdtToKafka(logAdt);
            }
            GoodsAddCartInfo goodsAddCartInfo = this.getCategoryListPageCartLogInfo(orderGoodInfo.getSku(), redisOrderInfo.getOrderInfo().getUserId());
            this.logger.info("pre_listpage?????? sku: {}???userid: {} ?????????,?????????????????????????????????: {}", orderGoodInfo.getSku(), redisOrderInfo.getOrderInfo().getUserId(), goodsAddCartInfo);
            if (goodsAddCartInfo != null) {
                this.logger.info("listpage?????????????????????????????????");
                String orderStatus = redisOrderInfo.getOrderInfo().getOrderStatus();
                goodsAddCartInfo.setSalesVolume(orderGoodInfo.getGoodsNum());
                if ("1".equals(orderStatus) || "8".equals(orderStatus)) {
                    this.logger.info("listpage??????????????????????????????????????????");
                    goodsAddCartInfo.setSalesAmount(orderGoodInfo.getAmount());
                    this.handleListPageOrderInfo(goodsAddCartInfo);
                }
            }
            GoodsAddCartInfo appGoodCartInfo = this.appGoodCartInfo(orderGoodInfo.getSku(), redisOrderInfo.getOrderInfo().getUserId());
            this.logger.info("pre_listpage?????? sku: {}???userid: {} ?????????,app ?????????????????????: {}", orderGoodInfo.getSku(), redisOrderInfo.getOrderInfo().getUserId(), goodsAddCartInfo);
            if (appGoodCartInfo != null) {
                this.logger.debug("?????? app ???????????????");
                Map<String, Object> outJson = new HashMap<>();
                outJson.put("bts", appGoodCartInfo.getBts());
                outJson.put(RecommendQuotaFields.exposure.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.skuClick.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.skuAddCart.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.specimen.recommendReportFields.name(), appGoodCartInfo.getCookie());
                outJson.put(RecommendQuotaFields.userOrder.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.paidOrder.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.payAmount.recommendReportFields.name(), 0);
                outJson.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
                String orderStatus = redisOrderInfo.getOrderInfo().getOrderStatus();

                if ("0".equals(orderStatus)) {
                    outJson.put(RecommendReportFields.sku_order_num.name(), 1);
                    //outJson.put(RecommendReportFields.sku_order_num.name(), orderGoodInfo.getGoodsNum());
                }
                if ("1".equals(orderStatus) || "8".equals(orderStatus)) {
                    outJson.put(RecommendReportFields.paid_order_num.name(), 1);
                    outJson.put(RecommendReportFields.paid_amount.name(), orderGoodInfo.getAmount());
                }

                this.send("dy_bts_zaful_re_simple_report", outJson);
            }
        }
    }

    private GoodsAddCartInfo appGoodCartInfo(String sku, String userId) {
        String key = CartRedisKeysPrefix.redisCartKey(CartRedisKeysPrefix.dyZafulAppCartInfo, userId, sku);
        this.logger.info("??? redis ?????? app ????????????????????????: {}", key);
        String listPageOrder = SpringRedisUtil.getStringValue(key);
        if (StringUtils.isNotBlank(listPageOrder)) {
            return GsonUtil.readValue(listPageOrder, GoodsAddCartInfo.class);
        }
        return null;
    }

    private Map<String, Object> order(OrderGoodInfo orderGoodInfo, RedisOrderInfo redisOrderInfo) {
        Map<String, Object> order = new HashMap<>();
        order.put(this.logAdtOrderId, redisOrderInfo.getOrderInfo().getOrderId());
        String orderStatus = redisOrderInfo.getOrderInfo().getOrderStatus();
        order.put(this.logAdtOrderStatusKey, orderStatus);
        order.put(this.goodsNumKey, orderGoodInfo.getGoodsNum());
        if ("1".equals(orderStatus) || "8".equals(orderStatus)) {
            order.put(this.logAdtOrderAmountKey, orderGoodInfo.getAmount());
        } else {
            order.put(this.logAdtOrderAmountKey, 0);
        }
        return order;
    }

    /**
     * ??? redis ???????????????????????????????????????
     * @param sku
     * @param userId
     * @return
     */
    private GoodsAddCartInfo getCategoryListPageCartLogInfo(String sku, String userId) {
        String key = "dy_zaful_adt_" + "c_list_" + userId + "_" + sku;
        this.logger.info("??? redis ?????????????????????????????????: {}", key);
        String listPageOrder = SpringRedisUtil.getStringValue(key);
        if (StringUtils.isNotBlank(listPageOrder)) {
            return GsonUtil.readValue(listPageOrder, GoodsAddCartInfo.class);
        }
        return null;
    }

    /**
     * ??????????????????????????????
     * @param goodsAddCartInfo
     * @throws Exception
     */
    private void handleListPageOrderInfo(GoodsAddCartInfo goodsAddCartInfo) throws Exception {
        this.logger.info("listpage????????????????????? kafka");
        BtsZafulListPageReport btsZafulListPageReport = new BtsZafulListPageReport();
        btsZafulListPageReport.setSalesVolume(goodsAddCartInfo.getSalesVolume());
        btsZafulListPageReport.setSalesAmount(goodsAddCartInfo.getSalesAmount());
        btsZafulListPageReport.setBts(goodsAddCartInfo.getBts());
        this.send(listPageReportTopic, btsZafulListPageReport);
    }

    /**
     * ??????????????? kafka ?????????????????? topic ????????????????????????
     *
     * @param logAdt
     */
    private void sendLogAdtToKafka(Map<String, Object> logAdt) throws Exception {
        for (String topic : this.topics) {
            if (topic.indexOf(this.btsKey) > 0) {
                this.logger.info("?????? bts ????????????");
                Map<String, String> btsInfo = this.btsPlanInfoQuery.queryBtsInfo(logAdt);
                String recommendType = RecommendTypeUtil.getRecommendTypeByFmd(String.valueOf(logAdt.get(this.fmd)));
                String cookie = String.valueOf(logAdt.get(filedKeys.glb_od.name()));
                if (btsInfo != null) {
                    this.logger.info("bts ??????????????????: {}", btsInfo);
                    logAdt.put(this.btsKey, btsInfo);
                    if ("dy_bts_zaful_re_simple_report".equals(topic)) {
                        Map<String, Object> outJson = new HashMap<>();
                        outJson.put(this.btsKey, btsInfo);
                        outJson.put(RecommendQuotaFields.specimen.recommendReportFields.name(), cookie);
                        for (RecommendQuotaFields reportFields : RecommendQuotaFields.values()) {

                           /* if (ValueType.value.equals(reportFields.valueType.name())) {
                                outJson.put(reportFields.recommendReportFields.name(), logAdt.get(reportFields.quota));
                            }*/
                            if (ValueType.num.name().equals(reportFields.valueType.name())) {
                                outJson.put(reportFields.recommendReportFields.name(), 0);
                            }

                        }

                        Map<String, Object> order = (Map<String, Object>) logAdt.get(this.logAdtOrderKey);
                        if (order != null) {
                            if ("0".equals(String.valueOf(order.get(this.logAdtOrderStatusKey)))) {
                                outJson.put(RecommendReportFields.sku_order_num.name(), 1);
                            }
                            if ("1".equals(String.valueOf(order.get(this.logAdtOrderStatusKey))) ||
                                    "8".equals(String.valueOf(order.get(this.logAdtOrderStatusKey)))) {
                                outJson.put(RecommendReportFields.paid_order_num.name(), 1);
                                outJson.put(RecommendReportFields.paid_amount.name(), order.getOrDefault(this.logAdtOrderAmountKey, 0));
                            }
                            outJson.put("timestamp", logAdt.get("glb_tm"));
                            this.send(topic, outJson);
                        }

                    } else {
                        this.send(topic, logAdt);
                    }
                } else {
                    this.logger.info("?????????????????????????????? bts ????????????, cookie: {},recommendType: {}", cookie, recommendType);
                }
            } else {
                Map<String, Object> order = (Map<String, Object>) logAdt.get(this.logAdtOrderKey);
                logAdt.put("goods_num", order.get(this.goodsNumKey) != null? order.get(this.goodsNumKey):0);
                this.send(topic, logAdt);
            }
        }
    }

    private Map<String, String> getBtsInfoFromUbcta(String ubcta) {
        this.logger.info("uncta ??????: {}", ubcta);
        if (ubcta.startsWith("[")) {
            List<Map<String, String>> mapList = GsonUtil.readValue(ubcta, List.class);
            if (mapList.size() > 0) {
                return this.buildBtsMap(mapList.get(0));
            }
        }
        if (ubcta.startsWith("{")) {
            return this.buildBtsMap(GsonUtil.readValue(ubcta, Map.class));
        }
        return null;
    }

    private Map<String, String> buildBtsMap(Map<String, String> ubMap) {
        this.logger.info("???????????? ubcta ??????: {}", ubMap);
        String planid = ubMap.get(zafulBts.planid.name());
        String versionId = ubMap.get(zafulBts.versionid.name());
        String bucketid = ubMap.get(zafulBts.bucketid.name());
        if (StringUtils.isNotEmpty(planid) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketid)) {
            Map<String, String> btsMap = new HashMap<>();
            btsMap.put(bts.plan_id.name(), planid);
            btsMap.put(bts.version_id.name(), versionId);
            btsMap.put(bts.bucket_id.name(), bucketid);
            return btsMap;
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param topic
     * @param logAdt
     */
    private void send(String topic, Object logAdt) throws Exception {
        this.logger.info("send order info to kafka topic: {}", topic);
        String json = JacksonUtil.toSortedJson(logAdt);

        String redisMd5 = "dy_zf_od_distinct_" + MD5CipherUtil.generatePassword(json) + topic;

        String check = SpringRedisUtil.getStringValue(redisMd5);
        if (StringUtils.isEmpty(check)) {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, json);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    logger.info("log json with dimensions send success!");
                }

                @Override
                public void onFailure(Throwable ex) {
                    logger.error("log json with dimensions send failed! msg: {}", json, ex);
                }
            });
            SpringRedisUtil.put(redisMd5, "1", this.expireSeconds);
        } else {
            this.logger.info("??????????????? kafka ???????????????: {}", json);
        }


    }


    /**
     * ?????? sku ??? user id ?????????????????????????????????
     *
     * @param sku
     * @param userId
     * @return
     * @throws Exception
     */
    private Map<String, Object> redisAdtData(String sku, String userId) throws Exception {
        String key = this.adtZafulPrefix + sku + "_" + userId;
        this.logger.info("redis log adt key: {}", key);
        String adtInfo = SpringRedisUtil.getStringValue(key);
        if (StringUtils.isNotEmpty(adtInfo)) {
            Map<String, Object> map = JacksonUtil.readValue(adtInfo, Map.class);
            map.put(this.logTimestampKey, System.currentTimeMillis());
            return map;
        }
       /* Set<String> keys = SpringRedisUtil.getAllKeyByPrefix(this.adtZafulPrefix + sku + "_" + userId);
        if (keys != null && keys.size() > 0) {
            String max = keys.stream().max((o1, o2) -> {
                String timestamp1 = o1.substring(o1.lastIndexOf("_") + 1);
                String timestamp2 = o2.substring(o2.lastIndexOf("_") + 1);
                //System.out.println( timestamp1 + "-" + timestamp2);
                if (NumberUtils.isDigits(timestamp1) && NumberUtils.isDigits(timestamp2)) {
                    Long t1 = Long.valueOf(timestamp1);
                    Long t2 = Long.valueOf(timestamp2);

                    if (t1 > t2) {
                        return 1;
                    }
                    if (t1 < t2) {
                        return -1;
                    }
                }
                return -1;
            }).orElse(null);
            if (StringUtils.isNotEmpty(max)) {
                String adtInfo = SpringRedisUtil.getStringValue(max);
                if (StringUtils.isNotEmpty(adtInfo)) {
                    Map<String,Object> map = JacksonUtil.readValue(adtInfo, Map.class);
                    map.put(this.logTimestampKey, System.currentTimeMillis());
                    return map;
                }
            }
        }*/
        return null;
    }


    private OrderGoodInfo orderGoodInfo(Map<String, Object> dataMap) {
        Map<String, Object> tableData = this.getEventData(dataMap);
        OrderGoodInfo orderGoodInfo = new OrderGoodInfo();
        orderGoodInfo.setOrderId(String.valueOf(tableData.get(OrderGoodInfo.ORDER_ID)));
        orderGoodInfo.setGoodsNum(Integer.valueOf(String.valueOf(tableData.get(OrderGoodInfo.GOODS_NUM))));
        orderGoodInfo.setSku(String.valueOf(tableData.get(OrderGoodInfo.SKU)));
        orderGoodInfo.setPrice(Float.valueOf(String.valueOf(tableData.get(OrderGoodInfo.PRICE))));
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
