package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.ListPageReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.model.DyReportKylinConfig;

public interface ListPageReportHandler{

    ReportPageDto reportCount(ListPageReportParameterDto listPageReportParameterDto, DyReportKylinConfig dyReportKylinConfig, String resolvedString, ReportPageDto mapReportPageDto);

}
