package com.globalegrow.controller;

import com.globalegrow.fixed.scheduler.BuryLogDataSyncByDay;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.annotation.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
@RequestMapping("bury-log")
@Data
@Slf4j
@Deprecated
public class BuryLogSycnController {

    @Autowired
    private BuryLogDataSyncByDay buryLogDataSyncByDay;

    @RequestMapping("trigger")
    public String trigger() throws InterruptedException {
        this.buryLogDataSyncByDay.run();
        return "success";
    }

    @RequestMapping("trigger_app")
    public String triggerApp() throws InterruptedException {
        this.buryLogDataSyncByDay.app();
        return "success";
    }

}
