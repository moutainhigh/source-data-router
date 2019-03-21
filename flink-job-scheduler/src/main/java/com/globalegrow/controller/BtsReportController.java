package com.globalegrow.controller;

import com.globalegrow.constants.BtsRecommendReportId;
import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("bts-report")
@Data
@Slf4j
public class BtsReportController {


    @GetMapping("add")
    public String addBtsPlanId(String planId, String platform) {
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(platform)) {
            StringBuilder stringBuilder = new StringBuilder(planId + ",");
            if ("pc".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
                if (StringUtils.isNotEmpty(content)) {
                    stringBuilder.append(content);
                }
                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdPc.getFilePath(), stringBuilder.toString());
            }
            if ("app".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
                if (StringUtils.isNotEmpty(content)) {
                    stringBuilder.append(content);
                }
                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdApp.getFilePath(), stringBuilder.toString());
            }
        }else {
            return "failed";
        }
        return "success";
    }

    @GetMapping("remove")
    public String removeBtsPlanId(String planId, String platform) {
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(platform)) {
            StringBuilder stringBuilder = new StringBuilder();
            if ("pc".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
                if (StringUtils.isNotEmpty(content)) {
                    content = content.replace(planId, "").replaceAll(",,",",");
                    stringBuilder.append(content);
                }
                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdPc.getFilePath(), stringBuilder.toString());
            }
            if ("app".equals(platform)) {
                String content = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
                if (StringUtils.isNotEmpty(content)) {
                    content = content.replace(planId, "").replaceAll(",,",",");
                    stringBuilder.append(content);
                }
                HdfsUtil.updateDyHdfsFile(BtsRecommendReportId.planIdApp.getFilePath(), stringBuilder.toString());
            }
        }else {
            return "failed";
        }
        return "success";
    }


}
