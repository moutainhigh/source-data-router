package com.globalegrow.controller;

import com.globalegrow.utils.FlinkJobStatusCheckUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("flink-job-status")
public class FlinkJobStatusController {

    @GetMapping
    public String jobStatus(String id) {
        return FlinkJobStatusCheckUtils.getJobStatusByJobId(id);
    }

}
