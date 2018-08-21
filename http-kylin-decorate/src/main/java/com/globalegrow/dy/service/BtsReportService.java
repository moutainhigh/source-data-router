package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.dto.KylinBtsReportDto;
import com.globalegrow.dy.dto.ReportPageDto;

import java.util.Map;

public interface BtsReportService {

    ReportPageDto<Map<String, Object>> btsReport(BtsReportParameterDto btsReportParameterDto);

}
