package com.globalegrow.report;

import java.util.Map;

public interface LogDataCache {

    /**
     * 将埋点数据进行缓存主要为加购数据缓存
     * @param reportName
     * @param jsonMap
     */
    void cacheData(String reportName, Map<String, Object> jsonMap, Long expireSeconds);

}
