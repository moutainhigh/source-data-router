package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.service.BtsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("report")
public class KylinReportController {

    @Autowired
    private BtsReportService btsReportService;

    @RequestMapping(value = "bts", produces="application/json;charset=UTF-8", method = RequestMethod.POST)
    public ReportPageDto<Map<String,Object>> btsReport(@RequestBody BtsReportParameterDto btsReportParameterDto) {
        return this.btsReportService.btsReport(btsReportParameterDto);
    }

}
