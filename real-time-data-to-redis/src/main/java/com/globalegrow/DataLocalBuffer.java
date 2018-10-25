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
    public void handleMsgAsync(String logString, LinkedBlockingDeque<QueenModel> linkedBlockingDeque) throws Exception {
        this.logWriteToRedis(logString).stream().forEach(m ->linkedBlockingDeque.offer(m));
    }

    public List<QueenModel> logWriteToRedis(String logString) throws Exception {
        if (logString.contains("/_app.gif?")) {
            Map value = AppLogConvertUtil.getAppLogParameters(logString);
            if (value != null) {
                String eventName = String.valueOf(value.get("event_name"));
                String eventValue = String.valueOf(value.get("event_value"));
                Map<String, Object> eventValueMap = null;
                try {
                    if (eventValue.startsWith("{:")) {
                        eventValue = NginxLogConvertUtil.handleBadJson(eventValue);
                    }
                    eventValueMap = JacksonUtil.readValue(eventValue, Map.class);
                } catch (Exception e) {
                    logger.error("解析 json 数据出错: {}", eventValue, e);
                }
                if (StringUtils.isNotEmpty(eventName) && eventValueMap != null) {
                    String deviceId = String.valueOf(value.get("appsflyer_device_id"));
                    String userId = String.valueOf(value.get("customer_user_id"));
                    String platform = String.valueOf(value.get("platform"));
                    String appName = String.valueOf(value.get("app_name"));
                    Long timestamp = (Long) value.get(NginxLogConvertUtil.TIMESTAMP_KEY);
                    try {
                        String valueNeeded = AppEventEnums.valueOf(eventName).getEventValueFromEventValue(eventValueMap);
                        if (StringUtils.isNotEmpty(valueNeeded) && eventValueMap != null && eventValueMap.size() > 0) {
                            List<QueenModel> list = new ArrayList<>();
                            for (String s : valueNeeded.split(",")) {
                                Map<String, Object> eventDataRow = new HashMap<>();
                                eventDataRow.put("event_name", eventName);
                                eventDataRow.put("event_value", s);
                                eventDataRow.put("user_id", userId);
                                eventDataRow.put("device_id", deviceId);
                                eventDataRow.put("platform", platform);
                                eventDataRow.put("site", SiteUtil.getAppSite(appName));
                                eventDataRow.put(NginxLogConvertUtil.TIMESTAMP_KEY, timestamp);
                                String key = redisKeyPrefix + deviceId + "_" + DateFormatUtils.format(timestamp, "yyyy-MM-dd");
                                QueenModel queenModel = new QueenModel(key, JacksonUtil.toJSon(eventDataRow));
                                list.add(queenModel);
                                //linkedBlockingDeque.put(queenModel);
                                return list;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        //logger.error("event {} not support yet!", eventName, e);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

}
