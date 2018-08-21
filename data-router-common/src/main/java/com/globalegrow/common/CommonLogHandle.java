package com.globalegrow.common;

import com.globalegrow.bts.model.BtsZafulListPageReport;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;

public abstract class CommonLogHandle extends CommonLogConvert{

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * kafka 消息处理通用流程方法
     * @param logString
     */
    public void handleLogData(String logString) {
        try {
            Map<String, Object> logMap = this.logToMap(logString);
            if (logMap != null && logMap.size() > 0) {
                if (this.logDataFilter(logMap)) {
                    Map<String, Object> reportData = this.reportData(logMap);
                    if (reportData != null && reportData.size() > 0) {
                        this.logger.debug("消息发送到 kafka，并填入时间戳字段");
                        reportData.put(NginxLogConvertUtil.TIMESTAMP_KEY, logMap.get(NginxLogConvertUtil.TIMESTAMP_KEY));
                        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(this.reportKafkaTopic(), GsonUtil.toJson(reportData));
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

    protected Map<String, Object> reportDataToMap(Object o) {
        return DyBeanUtils.objToMap(o);
    }

    /**
     * 最终输出的报表数据结构
     * @param logMap
     * @return
     */
    protected abstract Map<String, Object> reportData(Map<String, Object> logMap);

    /**
     * 报表数据过滤，如站点过滤等
     * @param logMap
     * @return
     */
    protected abstract boolean logDataFilter(Map<String, Object> logMap);

    /**
     * 报表输出 topic
     * @return
     */
    protected abstract String reportKafkaTopic();

}
