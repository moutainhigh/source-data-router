package com.globalegrow.dy.bts.kafka;

import com.globalegrow.bts.model.BtsZafulListPageReport;
import com.globalegrow.bts.model.GoodsAddCartInfo;
import com.globalegrow.hbase.HbaseQuery;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * zaful 列表页报表处理逻辑
 */
@Component
public class ZafulListPageReportListener extends BtsListener {

    public static final String COLUMN_FAMILY="cookie_userid";

    public static final String CU_TABLE_NAME="dy_cookie_userid_rel";

    public static final String LOG_REDIS_KEY_START="dy_zaful_adt_";

    @Value("${app.kafka.zaful-list-page-topic}")
    private String zafulListPageReportTopic;

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch2 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch3 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch4 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch5 = new CountDownLatch(1);

    @Autowired
    private HbaseQuery hbaseQuery;
    @Value("${app.redis.zaful-list-adt-expired-seconds:604800}")
    private Long expiredSeconds;

    @KafkaListener(/*topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}"*//*, partitions = {"6","7"}*//*)},*/topics = {"${app.kafka.log-source-topic}"},  groupId = "bts_zaful_list_page_report")
    public void listen1(String logString) throws Exception {
        this.logger.debug("customer thread 1");
        this.handleLogData(logString);
        this.countDownLatch1.countDown();
    }

    /*@KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"8","9"})}, groupId = "bts_zaful_list_page_report")
    public void listen2(String logString) throws Exception {
        this.logger.debug("customer thread 2");
        this.handleLogData(logString);
        this.countDownLatch2.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"4","5"})}, groupId = "bts_zaful_list_page_report")
    public void listen3(String logString) throws Exception {
        this.logger.debug("customer thread 3");
        this.handleLogData(logString);
        this.countDownLatch3.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"2","3"})}, groupId = "bts_zaful_list_page_report")
    public void listen4(String logString) throws Exception {
        this.logger.debug("customer thread 4");
        this.handleLogData(logString);
        this.countDownLatch4.countDown();
    }*/

    /**
     *
     * @throws Exception
     */
    //@KafkaListener(topics = {"${app.kafka.log-source-topic}"}, groupId = "bts_zaful_list_page_report")
    /*@KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"0","1"})}, groupId = "bts_zaful_list_page_report")
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
    protected Map<String, Object> reportData(Map<String, Object> logMap) {
        Map<String, String> btsInfo = this.btsInfo(logMap);
        if (btsInfo != null) {
            String glbS = String.valueOf(logMap.get("glb_s"));
            String glbPlf = String.valueOf(logMap.get("glb_plf"));
            String glbFilter = String.valueOf(logMap.get("glb_filter"));
            String glbX = String.valueOf(logMap.get("glb_x"));
            String glbSkuInfo = String.valueOf(logMap.get("glb_skuinfo"));
            String glbUbcta = String.valueOf(logMap.get("glb_ubcta"));
            String glbOd = String.valueOf(logMap.get("glb_od"));
            String glbT = String.valueOf(logMap.get("glb_t"));
            String glbU = String.valueOf(logMap.get("glb_u"));

            boolean isListPage = "b01".equals(glbS) && "pc".equals(glbPlf);
            boolean isAddListCart = "ic".equals(glbT) && "ADT".equals(glbX);
            boolean isGoodClick = "ic".equals(glbT) && ("sku".equals(glbX) || "addtobag".equals(glbX));
            if (isListPage) {
                BtsZafulListPageReport reportData = new BtsZafulListPageReport();
                reportData.setBts(btsInfo);
                // 分类列表页总 uv
                reportData.setListPageUv(glbOd);
                // 实验样本量 glb_filter: {"view":60,"sort":"Recommend","page":1}
                if (StringUtils.isNotEmpty(glbFilter)) {
                    Map<String, Object> filter = GsonUtil.readValue(glbFilter, Map.class);
                    if (filter != null && filter.size() > 0) {
                        String sort = String.valueOf(filter.get("sort"));
                        if ("Recommend".equals(sort)) {
                            this.logger.debug("样本量指标");
                            // 样本量
                            reportData.setSpecimen(glbOd);
                            reportData.setListPageRecommendUv(glbOd);
                            // 商品曝光数
                            if ("ie".equals(glbT)) {
                                this.logger.debug("曝光数指标");
                                List<Map<String, String>> ubc = this.ubcList(glbUbcta);
                                if (ubc != null && ubc.size() > 0) {
                                    reportData.setExposure(ubc.size());
                                }
                            }
                            // 商品点击数
                            if (isGoodClick) {
                                this.logger.debug("商品点击数指标");
                                Map<String, String> ubc = this.ubcMap(glbUbcta);
                                if (ubc != null && ubc.size() > 0) {
                                    if (!ubc.containsKey("sckw")) {
                                        this.logger.debug("glb_ubcta 中没有 sckw 字段");
                                        reportData.setGoodClick(1);
                                    }
                                }
                            }
                        }
                    }
                }
                return this.reportDataToMap(reportData);
            } else if (isAddListCart) {
                BtsZafulListPageReport reportData = new BtsZafulListPageReport();
                reportData.setBts(btsInfo);
                this.logger.debug("购物车事件处理");
                // 获取到 user id，并将架构事件放入redis
                Map<String, String> ubc = this.ubcMap(glbUbcta);
                if (ubc != null && ubc.size() > 0 && "Recommend".equals(ubc.get("sort"))) {
                    if (!ubc.containsKey("sckw") && "mp".equals(ubc.get("fmd"))) {
                        this.logger.debug("glb_ubcta 中无 sckw ，fmd=mp");
                        Map<String, Object> skuInfo = this.skuInfoMap(glbSkuInfo);
                        String sku = String.valueOf(skuInfo.get("sku"));
                        if (skuInfo != null) {
                            this.logger.debug("从 skuinfo 中获取加购数");
                            String pam = String.valueOf(skuInfo.get("pam"));
                            if (StringUtils.isNotBlank(pam)) {
                                Integer cartNum = Integer.valueOf(pam);
                                if (cartNum <= 50) {
                                    reportData.setAddCart(cartNum);
                                    String userId = "";
                                    if (StringUtils.isNotBlank(glbU) && !"null".equals(glbU)) {
                                        userId = glbU;
                                    }else {
                                        userId = this.queryUserId(glbOd);
                                    }
                                    if (StringUtils.isNotBlank(userId)) {
                                        this.logger.info("加购事件放入 redis: {}", glbOd);
                                        String redisKey = this.LOG_REDIS_KEY_START + "c_list_" + userId + "_" + sku;
                                        GoodsAddCartInfo goodsAddCartInfo = new GoodsAddCartInfo(glbOd, userId, sku, cartNum, btsInfo);
                                        SpringRedisUtil.put(redisKey, GsonUtil.toJson(goodsAddCartInfo), expiredSeconds);
                                    }
                                }

                            }
                        }
                    }
                }
                return this.reportDataToMap(reportData);
            }
        }
        this.logger.debug("bts 试验信息为空");
        return null;
    }

    /**
     * 从 hbase 查询 userId
     * @param cookie
     * @return
     */
    private String queryUserId(String cookie) {
        this.logger.debug("查询 userid");
        Object obj = hbaseQuery.selectRowKeyFamilyColumn(CU_TABLE_NAME, cookie,
                "user_id", COLUMN_FAMILY);
        if (obj != null) {
            return String.valueOf(obj);
        }
        this.logger.info("加购事件未找到 userid, cookie: {}", cookie);
        return null;
    }


    private Map<String, Object> skuInfoMap(String skuInfo) {
        if (StringUtils.isNotBlank(skuInfo) && !"null".equals(skuInfo)) {
            return GsonUtil.readValue(skuInfo, Map.class);
        }
        return null;
    }

    private Map<String, Object> reportDataToMap(BtsZafulListPageReport btsZafulListPageReport) {
        return DyBeanUtils.objToMap(btsZafulListPageReport);
    }

    private Map<String, String> ubcMap(String ubcta) {
        if (StringUtils.isNotBlank(ubcta) && !"null".equals(ubcta)) {
            return GsonUtil.readValue(ubcta, Map.class);
        }
        return null;
    }

    private List<Map<String, String>> ubcList(String ubcta) {
        if (StringUtils.isNotBlank(ubcta) && !"null".equals(ubcta)) {
            return GsonUtil.readValue(ubcta, List.class);
        }
        return null;
    }

    /**
     * 报表数据过滤，如站点过滤等
     *
     * @param logMap
     * @return
     */
    @Override
    protected boolean logDataFilter(Map<String, Object> logMap) {
        if ("10013".equals(logMap.get("glb_d"))) {
            this.logger.debug("只处理 zaful 网站埋点数据");
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
        return this.zafulListPageReportTopic;
    }
}
