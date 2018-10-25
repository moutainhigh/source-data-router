package com.globalegrow.dy.dto;

import java.util.List;

public class ReportFieldConfigResultDto {

    private List<BtsReportFieldConfigDto> reportFields;

    public ReportFieldConfigResultDto(List<BtsReportFieldConfigDto> reportFields) {
        this.reportFields = reportFields;
    }

    public List<BtsReportFieldConfigDto> getReportFields() {
        return reportFields;
    }

    public void setReportFields(List<BtsReportFieldConfigDto> reportFields) {
        this.reportFields = reportFields;
    }
}
