package com.globalegrow.controller;

import com.globalegrow.constants.BtsRecommendReportId;
import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@RestController
@RequestMapping("bts-report")
@Data
@Slf4j
public class BtsReportController {


    @GetMapping("add")
    public String addBtsPlanId(String planId, String platform) {
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(platform)) {
            StringBuilder stringBuilder = new StringBuilder();
            if ("pc".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
                appendPlanId(planId, stringBuilder, content);

                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdPc.getFilePath(), stringBuilder.toString().replaceAll(",,", ","));
            }
            if ("app".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
                appendPlanId(planId, stringBuilder, content);

                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdApp.getFilePath(), stringBuilder.toString().replaceAll(",,", ","));
            }
        } else {
            return "failed";
        }
        return "success";
    }

    private void appendPlanId(String planId, StringBuilder stringBuilder, String content) {
        if (StringUtils.isNotEmpty(content)) {
            stringBuilder.append(content);
            if (!content.contains(planId)) {
                stringBuilder.append("," + planId);
            }
        } else {
            stringBuilder.append(planId);
        }
    }

    @GetMapping("remove")
    public String removeBtsPlanId(String planId, String platform) {
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(platform)) {
            StringBuilder stringBuilder = new StringBuilder();
            if ("pc".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
                if (StringUtils.isNotEmpty(content)) {
                    content = content.replace(planId, "").replaceAll(",,", ",");
                    stringBuilder.append(content);
                }
                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdPc.getFilePath(), stringBuilder.toString());
            }
            if ("app".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
                if (StringUtils.isNotEmpty(content)) {
                    content = content.replace(planId, "").replaceAll(",,", ",");
                    stringBuilder.append(content);
                }
                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdApp.getFilePath(), stringBuilder.toString());
            }
        } else {
            return "failed";
        }
        return "success";
    }

    @GetMapping("ids")
    public Map<String, List<String>> getAllCurrentBtsId() {
        Map<String, List<String>> map = new HashMap<>();
        String contentPc = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
        String contentApp = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
        map.put("pc", Arrays.stream(contentPc.split(",")).filter(s -> StringUtils.isNotEmpty(s)).collect(Collectors.toList()));
        map.put("app", Arrays.stream(contentApp.split(",")).filter(s -> StringUtils.isNotEmpty(s)).collect(Collectors.toList()));
        return map;
    }


}
