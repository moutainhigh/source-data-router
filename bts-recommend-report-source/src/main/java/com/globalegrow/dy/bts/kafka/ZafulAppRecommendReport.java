package com.globalegrow.dy.bts.kafka;

import com.globalegrow.bts.model.GoodsAddCartInfo;
import com.globalegrow.common.hbase.CommonHbaseMapper;
import com.globalegrow.dy.report.enums.RecommendQuotaFields;
import com.globalegrow.enums.CartRedisKeysPrefix;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * app 推荐位报表，不取推荐位字段，只取 bts 实验信息
 */
//@Component
public class ZafulAppRecommendReport extends CommonBtsAppLogHandle{

    @Value("${app.kafka.log-zaful-out-topic}")
    private String zafulRecommendReportTopic;

    @Value("${app.hbase.zfapp-userid-table}")
    private String hbaseTableName;

    @Value("${app.hbase.zfapp-user-clumn-family}")
    private String columnFamily;

    @Value("${app.redis.zfapp-adt-expired-seconds:604800}")
    private Long expiredSeconds;

    @Autowired
    private CommonHbaseMapper hbaseMapper;

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);

    @Override
    String kafkaReportTopic() {
        return this.zafulRecommendReportTopic;
    }

    @KafkaListener(topics = {"${app.kafka.log-source-topic}"}, groupId = "dy_zaful_app_recommend_report")
    public void listen(String log) {
        this.handleLogData(log);
        this.countDownLatch1.countDown();
    }

    /**
     * app bts 信息要在具体的事件value中取
     * 曝光：
     * 点击：
     * 加购：
     * 样本量：
     * ""af_bucket_id"" : ""10""
     * ""af_plan_id"" : ""6""
     * ""af_version_id"" : ""9""
     * "versionid":"97","bucketid":"10","planid":"13"
     *
     * @param eventValue
     * @return
     */
    @Override
    protected Map<String, String> appLogBtsInfo(Map<String, Object> eventValue) {
        Map<String, String> btsInfo = super.appLogBtsInfo(eventValue);
        if (btsInfo != null && btsInfo.size() > 0) {
            Map<String, String> btsOld = new HashMap<>();
            btsOld.put("plan_id", btsInfo.get("planid"));
            btsOld.put("version_id", btsInfo.get("versionid"));
            btsOld.put("bucket_id", btsInfo.get("bucketid"));
            return btsOld;
        }
        return null;
    }

    /**
     * 最终输出的报表数据结构
     *
     * @param logMap
     * @return
     */
    @Override
    protected Map<String, Object> reportData(Map<String, Object> logMap) {
        String eventValue = String.valueOf(logMap.get("event_value"));
        if (StringUtils.isNoneEmpty(eventValue) && !"null".equals(eventValue)) {
            Map<String, Object> eventValueMap = GsonUtil.readValue(eventValue, Map.class);
            Map<String, String> bts = this.appLogBtsInfo(eventValueMap);
            this.logger.debug("app 埋点中的 bts 实验信息: {}", bts);
            if (bts != null) {
                Map<String, Object> outJson = new HashMap<>();
                outJson.put("bts", bts);
                outJson.put(RecommendQuotaFields.exposure.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.skuClick.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.skuAddCart.recommendReportFields.name(), 0);
                this.logger.debug("app 埋点中的 bts 信息");
                String platform = String.valueOf(logMap.get("platform"));
                String eventName = String.valueOf(logMap.get("event_name"));
                String deviceId = String.valueOf(logMap.get("appsflyer_device_id"));
                String userId = String.valueOf(logMap.get("customer_user_id"));
                outJson.put(RecommendQuotaFields.specimen.recommendReportFields.name(), deviceId);

                // 页面 uv：af_view_home,样本量
                // 曝光数：af_impression
                if ("af_impression".equals(eventName)) {
                    this.logger.debug("处理 app 曝光数");
                    String contentIds = String.valueOf(eventValueMap.get("af_content_id"));
                    if (StringUtils.isNotEmpty(contentIds) && !"null".equals(contentIds)) {
                        this.logger.debug("曝光数：{}", contentIds.split(",").length);
                        outJson.put(RecommendQuotaFields.exposure.recommendReportFields.name(), contentIds.split(",").length);
                    }
                }
                // 点击数：af_view_product
                if ("af_view_product".equals(eventName)) {
                    this.logger.debug("处理 app 点击数");
                    if ("0".equals(String.valueOf(eventValueMap.get("af_changed_size_or_color")))) {
                        this.logger.debug("点击数 +1");
                        outJson.put(RecommendQuotaFields.skuClick.recommendReportFields.name(), 1);
                    }
                }
                // 加购数：af_add_to_bag
                if ("af_add_to_bag".equals(eventName)) {
                    this.logger.debug("处理 app 加购数");
                    String goodNum = String.valueOf(eventValueMap.get("af_quantity"));
                    if (StringUtils.isNotEmpty(goodNum) && !"null".equals(goodNum)) {
                        this.logger.debug("加购数  + {}", goodNum);
                        if (StringUtils.isEmpty(userId) || "null".equals(userId)) {
                            userId = this.queryUserId(deviceId);
                        }
                        int cartNum = Integer.valueOf(goodNum);
                        outJson.put(RecommendQuotaFields.skuAddCart.recommendReportFields.name(), cartNum);
                        if (StringUtils.isNotEmpty(userId) && !"null".equals(userId)) {
                            String sku = String.valueOf(eventValueMap.get("af_content_id"));
                            String redisKey = CartRedisKeysPrefix.redisCartKey(CartRedisKeysPrefix.dyZafulAppCartInfo, userId, sku);
                            this.logger.debug("app 加购事件放入 kafka, key: {}", redisKey);
                            SpringRedisUtil.put(redisKey, GsonUtil.toJson(new GoodsAddCartInfo(deviceId, userId, sku, cartNum, bts)), this.expiredSeconds);
                        }
                    }
                }

                outJson.put(RecommendQuotaFields.userOrder.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.paidOrder.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.payAmount.recommendReportFields.name(), 0);

                return outJson;
            }
        }
        return null;
    }

    private String queryUserId(String deviceId) {
        return ZafulRecommendCartHandle.getUserIdByDeviceId(deviceId, this.logger, this.hbaseMapper, this.hbaseTableName, this.columnFamily);
    }
}
