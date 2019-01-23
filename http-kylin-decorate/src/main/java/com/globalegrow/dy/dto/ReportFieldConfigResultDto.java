package com.globalegrow.dy.dto;

import java.util.List;

public class ReportFieldConfigResultDto {

    private String message;

    private List<BtsReportFieldConfigDto> reportFields;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
