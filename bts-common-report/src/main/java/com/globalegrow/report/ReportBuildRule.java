package com.globalegrow.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportBuildRule {

    private Boolean globaleFilter = false;

    private List<JsonLogFilter> globaleJsonFilters;

    private String valueEnum;

    private String reportName;

    private String description;

    private ReportKafkaConfig reportFromKafka;

    private List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

    private Map<String, Object> reportDefaultValues = new HashMap<>();

    public String getValueEnum() {
        return valueEnum;
    }

    public void setValueEnum(String valueEnum) {
        this.valueEnum = valueEnum;
    }

    public Boolean getGlobaleFilter() {
        return globaleFilter;
    }

    public void setGlobaleFilter(Boolean globaleFilter) {
        this.globaleFilter = globaleFilter;
    }

    public List<JsonLogFilter> getGlobaleJsonFilters() {
        return globaleJsonFilters;
    }

    public void setGlobaleJsonFilters(List<JsonLogFilter> globaleJsonFilters) {
        this.globaleJsonFilters = globaleJsonFilters;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getReportDefaultValues() {
        return reportDefaultValues;
    }

    public void setReportDefaultValues(Map<String, Object> reportDefaultValues) {
        this.reportDefaultValues = reportDefaultValues;
    }

    public List<ReportQuotaFieldConfig> getReportQuotaFieldConfigs() {
        return reportQuotaFieldConfigs;
    }

    public ReportKafkaConfig getReportFromKafka() {
        return reportFromKafka;
    }

    public void setReportFromKafka(ReportKafkaConfig reportFromKafka) {
        this.reportFromKafka = reportFromKafka;
    }

    public void setReportQuotaFieldConfigs(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {
        this.reportQuotaFieldConfigs = reportQuotaFieldConfigs;
    }
}
