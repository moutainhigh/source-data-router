package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.service.BtsReportConfigService;
import com.globalegrow.dy.service.BtsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("report/bts")
public class KylinReportController {

    @Autowired
    private BtsReportService btsReportService;

    @Autowired
    private BtsReportConfigService btsReportConfigService;

    @RequestMapping(produces="application/json;charset=UTF-8", method = RequestMethod.POST)
    public ReportPageDto<Map<String,Object>> btsReport(@RequestBody BtsReportParameterDto btsReportParameterDto) {
        return this.btsReportService.btsReport(btsReportParameterDto);
    }

    @RequestMapping(value = "config", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public ReportFieldConfigResultDto fieldConfigDtos(@RequestBody FieldConfigParameterDto dto) {
        return new ReportFieldConfigResultDto(this.btsReportConfigService.btsReportFieldConfig(dto.getPlanId(), dto.getProductLineCode()));
    }

}
