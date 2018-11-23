package com.globalegrow.common;

import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.NginxLogConvertUtil;

import java.util.Map;

public abstract class CommonLogHandle extends CommonLogConvertAndSend{

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
     * @param logMap
     * @return
     */
    protected abstract Map<String, Object> reportData(Map<String, Object> logMap) throws Exception;

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
