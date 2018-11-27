package com.globalegrow.report;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JsonLogFilter {

    private String jsonPath;

    private String valueFilter;

    private String filterRule = "equals";

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getValueFilter() {
        return valueFilter;
    }

    public void setValueFilter(String valueFilter) {
        this.valueFilter = valueFilter;
    }

    public String getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(String filterRule) {
        this.filterRule = filterRule;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("jsonPath", jsonPath)
                .append("valueFilter", valueFilter)
                .append("filterRule", filterRule)
                .toString();
    }
}
