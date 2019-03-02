package com.globalegrow.controller;

import com.globalegrow.fixed.queen.AbstractFlinkJobQueen;
import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.DelayQueue;

@RestController
@RequestMapping("delay-job")
public class FlinkJobDelayController {

    @Autowired
    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    @GetMapping
    public Object[] getDelayJobs() {
        return flinkJobQueens.toArray();
    }

}
