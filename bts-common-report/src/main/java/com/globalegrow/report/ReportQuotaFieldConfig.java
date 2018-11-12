package com.globalegrow.report;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class ReportQuotaFieldConfig {

    private String quotaFieldName;

    private Object defaultValue;

    /**
     * 从源数据获取值的 json 路径
     */
    private String extractValueJsonPath;

    private List<JsonLogFilter> jsonLogFilters = new ArrayList<>();

    private String valueEnum;

    public String getExtractValueJsonPath() {
        return extractValueJsonPath;
    }

    public void setExtractValueJsonPath(String extractValueJsonPath) {
        this.extractValueJsonPath = extractValueJsonPath;
    }

    public List<JsonLogFilter> getJsonLogFilters() {
        return jsonLogFilters;
    }

    public void setJsonLogFilters(List<JsonLogFilter> jsonLogFilters) {
        this.jsonLogFilters = jsonLogFilters;
    }

    public String getQuotaFieldName() {
        return quotaFieldName;
    }

    public void setQuotaFieldName(String quotaFieldName) {
        this.quotaFieldName = quotaFieldName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueEnum() {
        return valueEnum;
    }

    public void setValueEnum(String valueEnum) {
        this.valueEnum = valueEnum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("quotaFieldName", quotaFieldName)
                .append("defaultValue", defaultValue)
                .append("extractValueJsonPath", extractValueJsonPath)
                .append("jsonLogFilters", jsonLogFilters)
                .append("valueEnum", valueEnum)
                .toString();
    }
}
