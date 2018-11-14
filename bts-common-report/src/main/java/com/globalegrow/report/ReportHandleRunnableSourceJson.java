package com.globalegrow.report;

import com.globalegrow.util.JacksonUtil;

import java.util.Map;

public class ReportHandleRunnableSourceJson extends ReportHandleRunnable {


    public ReportHandleRunnableSourceJson(LogDataCache logDataCache, ReportBuildRule reportBuildRule) {
        super(logDataCache, reportBuildRule);
    }

    @Override
    public Map<String, Object> finalJsonMap(String source) throws Exception {
        return JacksonUtil.readValue(source, Map.class);
    }
}
