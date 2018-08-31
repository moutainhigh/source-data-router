package com.globalegrow.dy.controller;

import com.globalegrow.dy.service.BtsReportConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("report-config")
public class KylinConfigController {

    @Autowired
    private BtsReportConfigService btsReportConfigService;

    @RequestMapping("bts-kylin")
    public String removeBtsKylinConfig(Long planId, String productLineCode, String queryType) {
        this.btsReportConfigService.removeBtsReportKylinConfig(planId, productLineCode, queryType);
        return "success";
    }

    @RequestMapping("bts-field")
    public String removeBtsFieldConfig(Long planId, String produceLineCode) {
        this.btsReportConfigService.removeBtsReportFieldConfig(planId, produceLineCode);
        return "success";
    }


}
