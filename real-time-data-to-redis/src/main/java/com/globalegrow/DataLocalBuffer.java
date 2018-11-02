package com.globalegrow;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@EnableAsync
public class DataLocalBuffer {

    @Value("${app.redis.readtime.prefix:dy_real_time_}")
    private String redisKeyPrefix;

    @Value("${max-batch-size:10000}")
    private Integer maxBatchSize;

    protected static final Logger logger = LoggerFactory.getLogger(KafkaAppLogCustomer.class);

    @Async
    public void handleMsgAsync(String logString, LinkedBlockingDeque<Map> linkedBlockingDeque) throws Exception {
        this.logWriteToRedis(logString).stream().forEach(m -> linkedBlockingDeque.offer(m));
    }

    public List<Map> logWriteToRedis(String logString) throws Exception {
        if (logString.contains("/_app.gif?")) {
            AppLogReport appLogReport = new AppLogReport();
            appLogReport.setLogSource(logString);
            Map value = null;
            try {
                value = AppLogConvertUtil.getAppLogParameters(logString);
                appLogReport.setLogSourceMap(JacksonUtil.toJSon(value));
            } catch (Exception e) {
                appLogReport.setIsSuccessHandle(false);
                logger.error("解析源数据出错", e);
            }
            if (value != null) {
                String eventName = String.valueOf(value.get("event_name"));
                String eventValue = String.valueOf(value.get("event_value"));
                String deviceId = String.valueOf(value.get("appsflyer_device_id"));
                String userId = String.valueOf(value.get("customer_user_id"));
                String platform = String.valueOf(value.get("platform"));
                String appName = String.valueOf(value.get("app_name"));
                appLogReport.setDeviceId(deviceId);

                Map<String, Object> eventValueMap = null;
                try {
                    if (eventValue.startsWith("{:")) {
                        eventValue = NginxLogConvertUtil.handleBadJson(eventValue);
                    }
                    eventValueMap = JacksonUtil.readValue(eventValue, Map.class);
                } catch (Exception e) {
                    appLogReport.setIsSuccessHandleEvent(false);
                    logger.error("解析 json 数据出错: {}", eventValue, e);
                }
                if (StringUtils.isNotEmpty(eventName) && eventValueMap != null) {

                    String af_inner_mediasource = String.valueOf(eventValueMap.get("af_inner_mediasource"));
                    if ("recommend_homepage".equals(af_inner_mediasource)) {
                        appLogReport.setRecommendType(af_inner_mediasource);
                        Map<String, String> bts = appLogBtsInfo(eventValueMap);
                        if (bts != null) {
                            appLogReport.setPlanId(bts.get("planid"));
                            appLogReport.setVersionId(bts.get("versionid"));
                            appLogReport.setBucketId(bts.get("bucketid"));
                        }
                        // 曝光数./
                        if ("af_impression".equals(eventName)) {

                            String contentIds = String.valueOf(eventValueMap.get("af_content_id"));
                            if (StringUtils.isNotEmpty(contentIds) && !"null".equals(contentIds)) {
                                appLogReport.setExpCount(contentIds.split(",").length);
                            }

                        }

                        if ("af_view_product".equals(eventName)) {
                            if ("0".equals(String.valueOf(eventValueMap.get("af_changed_size_or_color")))) {
                                appLogReport.setClickCount(1);
                            }
                        }

                        if ("af_add_to_bag".equals(eventName)) {
                            String goodNum = String.valueOf(eventValueMap.get("af_quantity"));
                            if (StringUtils.isNotEmpty(goodNum) && !"null".equals(goodNum)) {
                                appLogReport.setAddCartCount(Integer.valueOf(goodNum));
                            }
                        }

                        if ("af_add_to_wishlist".equals(eventName)) {
                            appLogReport.setCollectCount(1);
                        }
                    }

                }
            }
            List<Map> maps = new ArrayList<>();
            Map map = DyBeanUtils.objToMap(appLogReport);
            if (value == null) {
                map.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
            }else {
                map.put(NginxLogConvertUtil.TIMESTAMP_KEY, (Long) value.get(NginxLogConvertUtil.TIMESTAMP_KEY));
            }
            maps.add(map);
            return maps;
        }
        return Collections.emptyList();
    }


    protected static Map<String, String> appLogBtsInfo(Map<String, Object> eventValue) {
        //String eventValue = String.valueOf(logMap.get("event_value"));
        String planId = String.valueOf(eventValue.get("af_plan_id"));
        String versionId = String.valueOf(eventValue.get("af_version_id"));
        String bucketId = String.valueOf(eventValue.get("af_bucket_id"));
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketId)
                && !"null".equals(planId) && !"null".equals(versionId) && !"null".equals(bucketId)) {
            Map<String, String> bts = new HashMap<>();
            bts.put("planid", planId);
            bts.put("versionid", versionId);
            bts.put("bucketid", bucketId);
            return bts;
        }
        //logger.error("bts 实验 id 为空");
        return null;
    }

}
