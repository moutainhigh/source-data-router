package com.globalegrow.dy.bts.kafka;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;

public abstract class CommonBtsAppLogHandle {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * app bts 信息要在具体的事件value中取
     * 曝光：
     * 点击：
     * 加购：
     * 样本量：
     * ""af_bucket_id"" : ""10""
     *         ""af_plan_id"" : ""6""
     *         ""af_version_id"" : ""9""
     *         "versionid":"97","bucketid":"10","planid":"13"
     * @param eventValue
     * @return
     */
    protected Map<String, String> appLogBtsInfo(Map<String, Object> eventValue){
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
        this.logger.debug("bts 实验 id 为空");
        return null;
    }

    /**
     * kafka 消息处理通用流程方法
     * @param logString
     */
    public void handleLogData(String logString) {
        try {
            Map<String, Object> logMap = this.appLogToMap(logString);
            if (logMap != null && logMap.size() > 0) {
                if (this.logDataFilter(logMap)) {
                    Map<String, Object> reportData = this.reportData(logMap);
                    if (reportData != null && reportData.size() > 0) {
                        this.logger.debug("消息发送到 kafka，并填入时间戳字段");
                        reportData.put(NginxLogConvertUtil.TIMESTAMP_KEY, logMap.get(NginxLogConvertUtil.TIMESTAMP_KEY));
                        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(this.kafkaReportTopic(), GsonUtil.toJson(reportData));
                        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                            @Override
                            public void onSuccess(SendResult<String, String> result) {
                                logger.debug("log json with dimensions send success!");
                            }

                            @Override
                            public void onFailure(Throwable ex) {
                                logger.error("log json with dimensions send failed! msg: {}", reportData, ex);
                            }
                        });

                    }
                }
            }
        } catch (Exception e) {
            this.logger.error("处理 kafka 日志消息出错: {}", logString, e);
        }
    }

    /**
     * 日志转换为map
     * @param appLog
     * @return
     */
    protected Map<String, Object> appLogToMap(String appLog) {
        return AppLogConvertUtil.getAppLogParameters(appLog);
    }

    abstract String kafkaReportTopic();

    /**
     * 最终输出的报表数据结构
     * @param logMap
     * @return
     */
    protected abstract Map<String, Object> reportData(Map<String, Object> logMap);

    public boolean logDataFilter(Map<String, Object> logMap) {
        return true;
    }

}
