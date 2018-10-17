package com.globalegrow;

import com.globalegrow.bts.model.BtsGbRecommendReport;
import com.globalegrow.enums.CartRedisKeysPrefix;
import com.globalegrow.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.elasticsearch.common.settings.Settings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Component
public class GbMysqlBinlog {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private Settings settings = Settings.builder()
            .put("client.transport.sniff", true).put("cluster.name", "esearch-ai-aws").build();
    TransportClient client = new PreBuiltTransportClient(settings)
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.31.51.179"), 9300))
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.31.51.59"), 9300))
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.31.51.250"), 9300));

    public GbMysqlBinlog() throws UnknownHostException {
    }

    @KafkaListener(topics = {"glbg-analitic-json-pc"}, groupId = "gb-m-cart-test")
    public void listen2(ConsumerRecord<String, String> record) {
        String mysqlBinLog = record.value();
        Map<String, Object> m = GsonUtil.readValue(mysqlBinLog, Map.class);
        if (m.get("glb_bts") != null && String.valueOf(m.get("glb_bts")).length() > 2 && "10002".equals(m.get("glb_d")) && "m".equals(m.get("glb_plf")) && "c".equals(m.get("glb_b"))
                && "ic".equals(m.get("glb_t")) && ("ADT".equals(m.get("glb_x")) || "BDR".equals(m.get("glb_x")) || "BTS".equals(m.get("glb_x")))) {
            this.logger.info("add_cart_gb_m:{}", mysqlBinLog);
            this.logger.info("add_cart_sku_num: {}", getSkuInfos(m).size());
          /*  Map<String, Object> map = getSkuInfos(m).get(0);
            GoodsAddCartInfo goodsAddCartInfo = new GoodsAddCartInfo("", "", "", Integer.valueOf(String.valueOf(m.get("pam"))), null);*/

            String userId = String.valueOf(m.get("glb_u"));
            if (StringUtils.isNotEmpty(userId)) {
                userId = getUserIdFromEs(m);
            }
            List<Map<String, Object>> skuInfos = getSkuInfos(m);
            if (StringUtils.isNotEmpty(userId)) {

                try {
                    String finalUserId = userId;
                    skuInfos.forEach(sku -> {
                        String skuValue = String.valueOf(sku.get("sku"));
                        String redisKey = "dy_gb_m" + finalUserId + "_" + skuValue;
                        this.logger.info("userid and cookie rel add_cart_to_redis_key: {}", redisKey);
                        GoodsAddCartInfo goodsAddCartInfo = new GoodsAddCartInfo(String.valueOf(m.get("glb_u")), finalUserId, skuValue, 0, GbBtsInfoUtil.gbBtsInfo(m));
                        SpringRedisUtil.put(redisKey, GsonUtil.toJson(goodsAddCartInfo), 1209600);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * 从 elasticsearch 查找 userid
     * @param map
     * @return
     */
    private String getUserIdFromEs(Map<String, Object> map) {
        String id = String.valueOf(map.get("glb_od")) + "_" + String.valueOf(map.get("glb_d")) + "_" + String.valueOf(map.get("glb_dc"));
        Map<String, Object> userId = getDoc("cookie-userid-rel", "userid", MD5CipherUtil.generatePassword(id));
        this.logger.info("userid and cookie rel, id: {}: {}", id, userId);
        if (userId != null) {
            return String.valueOf(userId.get("userid"));
        }
        return "";
    }

    public Map<String, Object> getDoc(String index, String type, String id) {
        return getResponse(index, type, id).getSourceAsMap();
    }

    public GetResponse getResponse(String index, String type, String id) {
        return client.prepareGet(index, type, id)
                .execute()
                .actionGet();
    }

    private static List<Map<String, Object>> getSkuInfos(Map<String, Object> map) {
        Object skuObj = map.get("glb_skuInfo");
        if (skuObj == null) {
            skuObj = map.get("glb_skuinfo");
        }
        String sourceSkuInfo = String.valueOf(skuObj);
        if (StringUtils.isNotEmpty(sourceSkuInfo) && !"null".equals(sourceSkuInfo)) {
            String skuInfo = jsonUnescape(sourceSkuInfo);
            if (skuInfo.startsWith("{")) {
                Map<String, Object> map1 = GsonUtil.readValue(skuInfo, Map.class);
                List<Map<String, Object>> list = new ArrayList<>();
                list.add(map1);
                return list;
            }
            if (skuInfo.startsWith("[")) {
                return GsonUtil.readValue(skuInfo, List.class);
            }
        }
        return Collections.EMPTY_LIST;
    }

    public static String jsonUnescape(String jsonSource) {
        String btsJson = StringEscapeUtils.unescapeJson(jsonSource);
        if (btsJson.startsWith("\"") && btsJson.endsWith("\"")) {
            String s = btsJson.replaceFirst("\"", "");
            return s.substring(0, btsJson.lastIndexOf("\"")).replaceAll("\\\\", "");
        }
        return btsJson;
    }

    /**
     * 处理订单
     * @param record
     */
    @KafkaListener(topics = {"dy_gb_mysql_binlog"})
    public void listen(ConsumerRecord<String, String> record) {
        String mysqlBinLog = record.value();
        Map<String, Object> dataMap = GsonUtil.readValue(mysqlBinLog, Map.class);
        // sku 下单数
        if ("insert".equals(dataMap.get("type")) && StringUtils.isNumeric(String.valueOf(dataMap.get("table")).replace("order_goods_", ""))) {
            Map<String, Object> tableData = (Map<String, Object>) dataMap.get("data");
            this.logger.info("db order data: {}", mysqlBinLog);
            PictureCounter pictureCounter = new PictureCounter();
            String userId = String.valueOf(((Double)tableData.get("user_id")).longValue());
            String sku = String.valueOf(tableData.get("goods_sn"));
            String redisKey = "dy_gb_m" + userId + "_" + sku;
            String redisCache = SpringRedisUtil.getStringValue(redisKey);
            this.logger.info("redis cache info, redis key: {} data: {}",redisKey, redisCache);
            if (StringUtils.isNotEmpty(redisCache)) {
                GoodsAddCartInfo goodsAddCartInfo = GsonUtil.readValue(redisCache, GoodsAddCartInfo.class);
                // 缓存付款 sku 数
                goodsAddCartInfo.setPam(goodsAddCartInfo.getPam() + 1);
                String orderId = String.valueOf(tableData.get("order_sn"));
                // 缓存订单商品金额
                String amountKey = "dy_gb_m_amount_" + orderId;

                String redisAmount = SpringRedisUtil.getStringValue(amountKey);
                String goodNum = String.valueOf(tableData.get("qty"));
                if (goodNum.indexOf(".") > 0) {
                    goodNum = goodNum.substring(0, goodNum.indexOf("."));
                }
                Float f = ((Float.valueOf(String.valueOf(tableData.get("price"))) * Integer.valueOf(goodNum)) * 100);
                if (StringUtils.isEmpty(redisAmount)) {
                    goodsAddCartInfo.setSalesAmount(f.intValue());
                    SpringRedisUtil.put(amountKey, GsonUtil.toJson(goodsAddCartInfo) + "", 1209600);
                    this.logger.info("支付金额缓存: {}, {}", amountKey, goodsAddCartInfo);
                    //SpringRedisUtil.put(amountKey, f.intValue() + "", 1209600);
                }else {
                    GoodsAddCartInfo goodsAddCartInfo1 = GsonUtil.readValue(redisAmount, GoodsAddCartInfo.class);
                    Integer intAmount = goodsAddCartInfo1.getSalesAmount() + f.intValue();
                    goodsAddCartInfo1.setSalesAmount(intAmount);
                    SpringRedisUtil.put(amountKey, GsonUtil.toJson(goodsAddCartInfo1) + "", 1209600);
                    this.logger.info("支付金额缓存: {}, {}", amountKey, goodsAddCartInfo1);
                    //SpringRedisUtil.put(amountKey, intAmount + "", 1209600);
                }

                pictureCounter.setSpecimen(goodsAddCartInfo.getCookie());
                pictureCounter.setSkuOrder(goodsAddCartInfo.getPam());
                Map reportMap = DyBeanUtils.objToMap(pictureCounter);
                reportMap.putAll(goodsAddCartInfo.getBts());
                reportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());

                this.send("bts-gb-gdd-m-pic-report-precisely", reportMap);

            }
            // 推荐位加购事件处理
            String recommendRedisKey = CartRedisKeysPrefix.redisCartKey(CartRedisKeysPrefix.dyGbCartInfo, userId, sku);
            String recommendRedisCache = SpringRedisUtil.getStringValue(recommendRedisKey);
            if (StringUtils.isNotEmpty(recommendRedisCache)) {
                com.globalegrow.bts.model.GoodsAddCartInfo goodsAddCartInfo = GsonUtil.readValue(recommendRedisCache, com.globalegrow.bts.model.GoodsAddCartInfo.class);
                // dy_bts_gb_gd_rec_report
                BtsGbRecommendReport btsGbRecommendReport = new BtsGbRecommendReport();
                btsGbRecommendReport.setBts(goodsAddCartInfo.getBts());
                btsGbRecommendReport.setSkuOrder(1);
                Map reportMap = DyBeanUtils.objToMap(btsGbRecommendReport);
                reportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
                this.send("dy_bts_gb_gd_rec_report", reportMap);

                //String orderId = String.valueOf(tableData.get("order_goods_id"));
                String orderId = String.valueOf(tableData.get("order_sn"));
                // 缓存订单商品金额
                String amountKey = "dy_gb_recommend_amount_" + orderId;

                String redisAmount = SpringRedisUtil.getStringValue(amountKey);
                String goodNum = String.valueOf(tableData.get("qty"));
                if (goodNum.indexOf(".") > 0) {
                    goodNum = goodNum.substring(0, goodNum.indexOf("."));
                }
                Float f = ((Float.valueOf(String.valueOf(tableData.get("price"))) * Integer.valueOf(goodNum)) * 100);
                if (StringUtils.isEmpty(redisAmount)) {
                    goodsAddCartInfo.setSalesAmount(f.intValue());
                    SpringRedisUtil.put(amountKey, GsonUtil.toJson(goodsAddCartInfo), 1209600);
                    this.logger.info("支付金额缓存: {}, {}", amountKey, goodsAddCartInfo);
                    //SpringRedisUtil.put(amountKey, f.intValue() + "", 1209600);
                }else {
                    com.globalegrow.bts.model.GoodsAddCartInfo goodsAddCartInfo1 = GsonUtil.readValue(redisAmount, com.globalegrow.bts.model.GoodsAddCartInfo.class);
                    Integer intAmount = goodsAddCartInfo1.getSalesAmount() + f.intValue();
                    goodsAddCartInfo1.setSalesAmount(intAmount);
                    SpringRedisUtil.put(amountKey, GsonUtil.toJson(goodsAddCartInfo1) + "", 1209600);
                    this.logger.info("支付金额缓存: {}, {}", amountKey, goodsAddCartInfo1);
                    //SpringRedisUtil.put(amountKey, intAmount + "", 1209600);
                }
            }
        }else
        // 订单金额
        if (StringUtils.isNumeric(String.valueOf(dataMap.get("table")).replace("order_info_", ""))) {
            Map<String, Object> tableData = (Map<String, Object>) dataMap.get("data");
            this.logger.info("order_info_event: {}", mysqlBinLog);
            String orderId = String.valueOf(tableData.get("order_sn"));
            String status = String.valueOf(tableData.get("pay_status"));

            if (status.indexOf(".") > 0) {
                status = status.substring(0, status.indexOf("."));
            }
            this.logger.info("order_info_event_status: {}", status);
            if ("1".equals(status) || "3".equals(status)){
                String amountKey = "dy_gb_m_amount_" + orderId;
                String redisAmount = SpringRedisUtil.getStringValue(amountKey);
                this.logger.info("paid_order: {}, {}", amountKey, redisAmount);
                if (StringUtils.isNotEmpty(redisAmount)) {
                    this.logger.info("paid_order 已支付 m 端订单信息: {}", redisAmount);
                    GoodsAddCartInfo goodsAddCartInfo1 = GsonUtil.readValue(redisAmount, GoodsAddCartInfo.class);
                    PictureCounter pictureCounter = new PictureCounter();

                    pictureCounter.setSpecimen(goodsAddCartInfo1.getCookie());
                    pictureCounter.setAmount(goodsAddCartInfo1.getSalesAmount());
                    pictureCounter.setPaidOrder(goodsAddCartInfo1.getPam());
                    Map reportMap = DyBeanUtils.objToMap(pictureCounter);
                    reportMap.putAll(goodsAddCartInfo1.getBts());
                    reportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());

                    this.send("bts-gb-gdd-m-pic-report-precisely", reportMap);

                }
                // 推荐位金额处理
                String recommendAmountKey = "dy_gb_recommend_amount_" + orderId;
                String redisRecommendAmount = SpringRedisUtil.getStringValue(recommendAmountKey);
                if (StringUtils.isNotEmpty(redisRecommendAmount)) {
                    this.logger.info("paid_order 已支付 推荐位订单信息: {}", redisRecommendAmount);
                    com.globalegrow.bts.model.GoodsAddCartInfo goodsAddCartInfo1 = GsonUtil.readValue(redisRecommendAmount, com.globalegrow.bts.model.GoodsAddCartInfo.class);
                    BtsGbRecommendReport btsGbRecommendReport = new BtsGbRecommendReport();
                    btsGbRecommendReport.setBts(goodsAddCartInfo1.getBts());
                    btsGbRecommendReport.setAmount(goodsAddCartInfo1.getSalesAmount());
                    btsGbRecommendReport.setSkuOrderPaid(1);
                    Map reportMap = DyBeanUtils.objToMap(btsGbRecommendReport);
                    reportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());

                    this.send("dy_bts_gb_gd_rec_report", reportMap);

                }
            }

        }

    }

    /**
     * m 端订单处理
     */
    private void mOrderHandle() {

    }

    /**
     * pc 推荐位订单处理
     */
    private void recommendHandle() {

    }

    private void send(String topic, Object msg) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, GsonUtil.toJson(msg));
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("log json with dimensions send success!");
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("log json with dimensions send failed! msg: {}", GsonUtil.toJson(msg), ex);
            }
        });
    }

    public static Map<String, String> defaultBtsTestInfo() {
        Map<String, String> fieldInfo = new HashMap<>();
        fieldInfo.put("bts_planid", "_skip");
        fieldInfo.put("bts_versionid", "_skip");
        fieldInfo.put("bts_bucketid", "_skip");
        fieldInfo.put("bts_policy", "_skip");
        fieldInfo.put("bts_plancode", "_skip");
        return fieldInfo;
    }

}
