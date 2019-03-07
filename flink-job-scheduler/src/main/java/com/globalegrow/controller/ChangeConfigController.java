package com.globalegrow.controller;

import com.globalegrow.fixed.scheduler.ApacheDrillHdfsActiveNameNodeCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("config")
public class ChangeConfigController {

    @Autowired
    private ApacheDrillHdfsActiveNameNodeCheck apacheDrillHdfsActiveNameNodeCheck;

    @GetMapping("drill")
    public String drillConfig(String drillStorage) {
        apacheDrillHdfsActiveNameNodeCheck.setApacheDrillAdress(drillStorage);
        return "success";
    }

}
