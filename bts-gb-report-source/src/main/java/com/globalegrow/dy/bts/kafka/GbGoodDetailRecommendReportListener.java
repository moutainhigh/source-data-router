package com.globalegrow.dy.bts.kafka;

import com.globalegrow.bts.model.BtsGbRecommendReport;
import com.globalegrow.bts.model.GoodsAddCartInfo;
import com.globalegrow.common.CommonBtsLogHandle;
import com.globalegrow.common.hbase.CommonHbaseMapper;
import com.globalegrow.enums.CartRedisKeysPrefix;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.MD5CipherUtil;
import com.globalegrow.util.SpringRedisUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
public class GbGoodDetailRecommendReportListener extends GbBtsInfo {

    @Value("${app.kafka.gb-good-detail-rec-topic}")
    private String goodDetailRecommendReportTopic;

   /* @Autowired
    private CommonHbaseMapper commonHbaseMapper;*/

    @Autowired
    private JestClient jestClient;

    @Value("${app.redis.gb-adt-expired-seconds:604800}")
    private Long expiredSeconds;

    @Value("${app.hbase.gb-cookie-userid-table}")
    private String hbaseTableName;

    @Value("${app.hbase.gb-cookie-user-clumn-family}")
    private String columnFamily;

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch2 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch3 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch4 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch5 = new CountDownLatch(1);


    @KafkaListener(topics = "${app.kafka.log-source-topic}", groupId = "bts_gb_gooddetail_recommend_report")
    public void listen1(String logString) throws Exception {
        this.logger.debug("customer thread 1");
        this.handleLogData(logString);
        this.countDownLatch1.countDown();
    }

    /*@KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"8","9"})}, groupId = "bts_gb_gooddetail_recommend_report")
    public void listen2(String logString) throws Exception {
        this.logger.debug("customer thread 2");
        this.handleLogData(logString);
        this.countDownLatch2.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"4","5"})}, groupId = "bts_gb_gooddetail_recommend_report")
    public void listen3(String logString) throws Exception {
        this.logger.debug("customer thread 3");
        this.handleLogData(logString);
        this.countDownLatch3.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"2","3"})}, groupId = "bts_gb_gooddetail_recommend_report")
    public void listen4(String logString) throws Exception {
        this.logger.debug("customer thread 4");
        this.handleLogData(logString);
        this.countDownLatch4.countDown();
    }

    *//**
     *
     * @throws Exception
     *//*
    //@KafkaListener(topics = {"${app.kafka.log-source-topic}"}, groupId = "bts_gb_gooddetail_recommend_report")
    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"0","1"})}, groupId = "bts_gb_gooddetail_recommend_report")
    public void listen5(String logString) throws Exception {
        this.logger.debug("customer thread 5");
        this.handleLogData(logString);
        this.countDownLatch5.countDown();
    }*/

    /**
     * 最终输出的报表数据结构
     *
     * @param logMap
     * @return
     */
    @Override
    protected Map<String, Object> reportData(Map<String, Object> logMap) throws Exception {
        Map<String, String> btsInfo = this.btsInfo(logMap);
        if (btsInfo != null) {
            String glbX = String.valueOf(logMap.get("glb_x"));
            String glbOd = String.valueOf(logMap.get("glb_od"));
            String glbT = String.valueOf(logMap.get("glb_t"));
            String glbU = String.valueOf(logMap.get("glb_u"));
            String glbSkuInfo = String.valueOf(logMap.get("glb_skuinfo"));
            String glbUbcta = String.valueOf(logMap.get("glb_ubcta"));
            String glbPm = String.valueOf(logMap.get("glb_pm"));
            String glbB = String.valueOf(logMap.get("glb_b"));
            String glbCl = String.valueOf(logMap.get("glb_cl"));

            boolean isGoodDetail = "c".equals(glbB);
            if (isGoodDetail) {
                this.logger.debug("处理商详页埋点数据");
                BtsGbRecommendReport btsGbRecommendReport = new BtsGbRecommendReport();
                btsGbRecommendReport.setBts(btsInfo);
                // 商详页 pv 数
                boolean isExposureEvent = "ie".equals(glbT);
                boolean isClickEvent = "ic".endsWith(glbT);
                boolean isGoodDetailPv = isExposureEvent && StringUtils.isNotEmpty(glbCl) && glbCl.indexOf("/pp_") > 0;
                boolean pmIsMr = "mr".equals(glbPm);
                if (isGoodDetailPv) {
                    btsGbRecommendReport.setGoodDetailPv(1);
                }
                // 商详 uv 数
                this.logger.debug("商详页 uv");
                btsGbRecommendReport.setGoodDetailUv(glbOd);
                // 曝光 pv 数,曝光 sku
                if (isExposureEvent && pmIsMr && StringUtils.isNotEmpty(glbUbcta) && !"null".equals(glbUbcta)) {
                    this.logger.debug("推荐位曝光 pv");
                    btsGbRecommendReport.setRecommendTypeExposurePv(1);
                    List<Map<String, String>> ubcs = GsonUtil.readValue(glbUbcta, List.class);
                    if (ubcs != null && ubcs.size() > 0) {
                        this.logger.debug("曝光商品数");
                        /*String policy = btsInfo.get("mdlc");
                        if (StringUtils.isNotEmpty(policy)) {
                            List<Map<String, String>> ubcsBts =  ubcs.stream().filter(e -> policy.equals(e.get("mrlc"))).collect(Collectors.toList());
                            btsGbRecommendReport.setSkuExposure(ubcsBts.size());
                        }else {
                            this.logger.warn("埋点中推荐位状态为空！");
                        }*/
                        btsGbRecommendReport.setSkuExposure(ubcs.size());
                    }
                    return this.reportDataToMap(btsGbRecommendReport);
                }
                // 点击 & 加购
                if (isClickEvent) {
                    this.logger.debug("点击事件");
                    if (pmIsMr && StringUtils.isNotEmpty(glbUbcta) && !"null".equals(glbUbcta) &&
                            StringUtils.isNotEmpty(glbSkuInfo) && !"null".equals(glbSkuInfo)) {
                        this.logger.debug("sku 点击事件");
                        btsGbRecommendReport.setSkuClick(1);
                        return this.reportDataToMap(btsGbRecommendReport);
                    }
                    if (("mb".equals(glbPm) || "mbt".equals(glbPm)) && ("ADT".equals(glbX) || "BDR".equals(glbX) || "BTS".equals(glbX))
                            && StringUtils.isNotEmpty(glbSkuInfo) && !"null".equals(glbSkuInfo)) {
                        this.logger.debug("sku 加购数");
                        if (StringUtils.isEmpty(glbU)) {
                            //glbU = this.queryUserId(glbOd);

                            String id = String.valueOf(logMap.get("glb_od")) + "_" + String.valueOf(logMap.get("glb_d")) + "_" + String.valueOf(logMap.get("glb_dc"));
                            this.logger.debug("根据 cookie 站点等信息查询用户 id 信息: {}", id);
                            Get get = new Get.Builder("cookie-userid-rel", MD5CipherUtil.generatePassword(id)).type("userid").build();
                            glbU = getUserIdFromEs(glbU, id, get);

                        }
                        if (glbSkuInfo.contains("[{")) {
                            this.logger.debug("buy together");
                            List<Map<String, Object>> skus = JacksonUtil.readValue(glbSkuInfo, List.class);
                            if (skus != null && skus.size() > 0) {
                                btsGbRecommendReport.setSkuAddCart(skus.size());
                                if (StringUtils.isNotEmpty(glbU)) {
                                    this.logger.debug("购物车信息放入 redis");
                                    String finalGlbU = glbU;
                                    skus.forEach(m -> {
                                        String goodSku = String.valueOf(m.get("sku"));
                                        this.cacheCartInfo(glbOd, finalGlbU, goodSku,1, btsInfo);
                                    });
                                }
                            }
                        } else {
                            this.logger.debug("单个商品加购");
                            Map<String, Object> sku = JacksonUtil.readValue(glbSkuInfo, Map.class);
                            if (sku != null && sku.size() > 0) {
                                btsGbRecommendReport.setSkuAddCart(1);
                                if (StringUtils.isNotEmpty(glbU)) {
                                    this.logger.debug("购物车信息放入 redis");
                                    String goodSku = String.valueOf(sku.get("sku"));
                                    this.cacheCartInfo(glbOd, glbU, goodSku,1, btsInfo);
                                }
                            }
                        }
                        return this.reportDataToMap(btsGbRecommendReport);
                    }
                }

            }

        }
        this.logger.debug("埋点中未找到 bts 实验信息: {}", logMap);
        return null;
    }

    private String getUserIdFromEs(String userId, String id, Get get) {
        try {
            DocumentResult result = this.jestClient.execute(get);
            if (result != null) {
                Map<String, String> cookieUserIdMap = result.getSourceAsObject(Map.class);
                this.logger.debug("根据 cookie 站点等信息查询用户 id 结果:{} ,{}", id, cookieUserIdMap);
                if (cookieUserIdMap != null) {
                    userId = cookieUserIdMap.get("userid");
                }
            }
        } catch (Exception e) {
            logger.error("查询用户id error", e);
        }
        return userId;
    }

    private void cacheCartInfo(String cookie, String userId, String sku, Integer pam, Map<String, String> bts) {
        String redisKey = CartRedisKeysPrefix.redisCartKey(CartRedisKeysPrefix.dyGbCartInfo, userId, sku);
        GoodsAddCartInfo goodsAddCartInfo = new GoodsAddCartInfo(cookie, userId, sku, pam, bts);
        SpringRedisUtil.put(redisKey, GsonUtil.toJson(goodsAddCartInfo), this.expiredSeconds);
    }

    /**
     * 报表数据过滤，如站点过滤等
     *
     * @param logMap
     * @return
     */
    @Override
    protected boolean logDataFilter(Map<String, Object> logMap) {
        if ("10002".equals(logMap.get("glb_d"))) {
            this.logger.debug("只处理 gb 网站埋点数据");
            return true;
        }
        return false;
    }

    /**
     * 报表输出 topic
     *
     * @return
     */
    @Override
    protected String reportKafkaTopic() {
        return this.goodDetailRecommendReportTopic;
    }


}
