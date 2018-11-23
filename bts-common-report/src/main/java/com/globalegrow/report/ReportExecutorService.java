package com.globalegrow.report;

import java.util.concurrent.ExecutorService;

public class ReportExecutorService {

    private ReportBuildRule reportBuildRule;

    private ExecutorService executorService;

    public ReportExecutorService(ReportBuildRule reportBuildRule, ExecutorService executorService) {
        this.reportBuildRule = reportBuildRule;
        this.executorService = executorService;
    }

    public ReportBuildRule getReportBuildRule() {
        return reportBuildRule;
    }

    public void setReportBuildRule(ReportBuildRule reportBuildRule) {
        this.reportBuildRule = reportBuildRule;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
