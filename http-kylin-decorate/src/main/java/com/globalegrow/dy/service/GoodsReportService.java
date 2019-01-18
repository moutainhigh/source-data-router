package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.GoodsReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;

public interface GoodsReportService {

    ReportPageDto goodsReport(GoodsReportParameterDto goodsReportParameterDto);

}
