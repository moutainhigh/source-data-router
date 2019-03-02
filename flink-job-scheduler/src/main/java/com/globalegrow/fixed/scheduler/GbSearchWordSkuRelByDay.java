package com.globalegrow.fixed.scheduler;

import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;

@Slf4j
//@Component
public class GbSearchWordSkuRelByDay {

    @Autowired
    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    @Scheduled(cron = "")
    public void run() {

    }

}
