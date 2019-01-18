package com.globalegrow.dy.report.model;

import java.util.Map;

public class ReportDataModel {

    public ReportDataModel(Map<String, Object> logData) {
        this.logData = logData;
    }

    private Map<String, Object> logData;

    public Map<String, Object> getLogData() {
        return logData;
    }

    public void setLogData(Map<String, Object> logData) {
        this.logData = logData;
    }


}
