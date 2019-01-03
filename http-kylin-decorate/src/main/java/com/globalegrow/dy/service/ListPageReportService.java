package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.ListPageReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;

public interface ListPageReportService {

    ReportPageDto listPageReport(ListPageReportParameterDto listPageReportParameterDto);

}
