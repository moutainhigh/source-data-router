package com.globalegrow.controller;


import com.globalegrow.constants.BtsRecommendReportId;
import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("zaful-rec-bts-page")
public class BtsReportPlanIdPage {

    @Autowired
    private BtsReportController btsReportController;

    @GetMapping("add")
    public String addBtsPlanId(String planId, String platform, Model model) {
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(platform)) {
            this.btsReportController.addBtsPlanId(planId, platform);
        }
        model.addAllAttributes(this.btsIds());
        return "index";
    }

    @GetMapping("remove")
    public String removeBtsPlanId(String planId, String platform, Model model) {
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(platform)) {
            this.btsReportController.removeBtsPlanId(planId, platform);
        }
        model.addAllAttributes(this.btsIds());
        return "index";
    }

    @GetMapping("ids")
    public String getAllCurrentBtsId(Model model) {
        model.addAllAttributes(this.btsIds());
        return "index";
    }

    private Map<String, String> btsIds() {
        Map<String, String> map = new HashMap<>();
        String contentPc = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
        String contentApp = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
        map.put("pc", contentPc);
        map.put("app", contentApp);
        return map;
    }


}
