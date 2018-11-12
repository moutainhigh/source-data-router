package com.globalegrow.report;

public class ReportKafkaConfig {

    private boolean fromStartOffset = false;

    private String bootstrapServers;

    private String bootstrapGroupId;

    private String dataSourceTopic;

    private String reportStrapServers;

    private String reportDataTopic;

    public String getBootstrapGroupId() {
        return bootstrapGroupId;
    }

    public void setBootstrapGroupId(String bootstrapGroupId) {
        this.bootstrapGroupId = bootstrapGroupId;
    }

    public boolean isFromStartOffset() {
        return fromStartOffset;
    }

    public void setFromStartOffset(boolean fromStartOffset) {
        this.fromStartOffset = fromStartOffset;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getDataSourceTopic() {
        return dataSourceTopic;
    }

    public void setDataSourceTopic(String dataSourceTopic) {
        this.dataSourceTopic = dataSourceTopic;
    }

    public String getReportStrapServers() {
        return reportStrapServers;
    }

    public void setReportStrapServers(String reportStrapServers) {
        this.reportStrapServers = reportStrapServers;
    }

    public String getReportDataTopic() {
        return reportDataTopic;
    }

    public void setReportDataTopic(String reportDataTopic) {
        this.reportDataTopic = reportDataTopic;
    }
}
