package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.GoodsReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.model.DyReportKylinConfig;

public interface GoodsReportHandler {

    ReportPageDto reportCount(GoodsReportParameterDto goodsReportParameterDto, DyReportKylinConfig dyReportKylinConfig, String resolvedString, ReportPageDto mapReportPageDto);

}
