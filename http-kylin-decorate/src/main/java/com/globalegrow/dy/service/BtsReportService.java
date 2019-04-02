package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.dto.KylinBtsReportDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.model.BtsReportKylinConfig;

import java.util.Map;

public interface BtsReportService {

    ReportPageDto btsReport(BtsReportParameterDto btsReportParameterDto) throws Exception;

    ReportPageDto btsReport(BtsReportKylinConfig btsReportKylinConfig, BtsReportParameterDto btsReportParameterDto) throws Exception;

}
