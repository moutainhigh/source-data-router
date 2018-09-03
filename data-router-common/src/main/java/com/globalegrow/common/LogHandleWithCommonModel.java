package com.globalegrow.common;

import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.NginxLogConvertUtil;

import java.util.Map;

public abstract class LogHandleWithCommonModel  extends CommonLogConvertAndSend{

    /**
     * kafka 消息处理通用流程方法
     * @param logString
     */
    public void handleLogData(String logString) {
        try {
            Map<String, Object> logMap = this.logToMap(logString);
            CommonLogModel commonLogModel = this.commonLogModel(logMap);
            if (logMap != null && logMap.size() > 0) {
                if (this.logDataFilter(commonLogModel)) {
                    Map<String, Object> reportData = this.reportData(commonLogModel);
                    if (reportData != null && reportData.size() > 0) {
                        this.logger.debug("消息发送到 kafka，并填入时间戳字段");
                        reportData.put(NginxLogConvertUtil.TIMESTAMP_KEY, logMap.get(NginxLogConvertUtil.TIMESTAMP_KEY));
                        this.sendToKafka(this.reportKafkaTopic(), reportData);
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
     * @param commonLogModel
     * @return
     */
    protected abstract Map<String, Object> reportData(CommonLogModel commonLogModel);

    /**
     * 报表数据过滤，如站点过滤等
     * @param commonLogModel
     * @return
     */
    protected abstract boolean logDataFilter(CommonLogModel commonLogModel);

    /**
     * 报表输出 topic
     * @return
     */
    protected abstract String reportKafkaTopic();

}
