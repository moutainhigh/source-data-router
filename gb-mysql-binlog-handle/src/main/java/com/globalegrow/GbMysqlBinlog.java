package com.globalegrow;

import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.globalegrow.util.SpringRedisUtil;
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
                        this.logger.info("redis key: {}", redisKey);
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
        Map<String, Object> userId = getDoc("cookie-userid-rel", "userid", String.valueOf(map.get("glb_od")) + "_" + String.valueOf(map.get("glb_d")) + "_" + String.valueOf(map.get("glb_dc")));
        this.logger.info("userid and cookie rel: {}", userId);
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
        if ("insert".equals(dataMap.get("type")) && StringUtils.isNumeric(String.valueOf(dataMap.get("table")).replace("order_goods_", ""))) {

            PictureCounter pictureCounter = new PictureCounter();
            String userId = String.valueOf(dataMap.get("user_id"));
            String sku = String.valueOf(dataMap.get("goods_sn"));
            String redisKey = "dy_gb_m" + userId + "_" + sku;
            String redisCache = SpringRedisUtil.getStringValue(redisKey);
            this.logger.info("redis cache info, redis key: {} data: {}",redisKey, redisCache);
            if (StringUtils.isNotEmpty(redisCache)) {
                GoodsAddCartInfo goodsAddCartInfo = GsonUtil.readValue(redisCache, GoodsAddCartInfo.class);
                pictureCounter.setSpecimen(goodsAddCartInfo.getCookie());
                pictureCounter.setSkuOrder(1);
                Map reportMap = DyBeanUtils.objToMap(pictureCounter);
                reportMap.putAll(goodsAddCartInfo.getBts());
                reportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());

                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("bts-gb-gdd-m-pic-report-precisely", GsonUtil.toJson(reportMap));
                future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        logger.info("log json with dimensions send success!");
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error("log json with dimensions send failed! msg: {}", GsonUtil.toJson(reportMap), ex);
                    }
                });

            }/*else {
                Map reportMap = DyBeanUtils.objToMap(pictureCounter);
                reportMap.putAll(defaultBtsTestInfo());
                reportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("bts-gb-gdd-m-pic-report-precisely", GsonUtil.toJson(reportMap));
                future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        logger.info("log json with dimensions send success!");
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error("log json with dimensions send failed! msg: {}", GsonUtil.toJson(reportMap), ex);
                    }
                });
            }*/

        }

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
