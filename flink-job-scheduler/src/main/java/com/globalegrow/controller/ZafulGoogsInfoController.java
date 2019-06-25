package com.globalegrow.controller;

import com.globalegrow.fixed.scheduler.ZafulGoodsInfoByDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("zaful-goods-trigger")
public class ZafulGoogsInfoController {


    @Autowired
    private ZafulGoodsInfoByDay zafulGoodsInfoByDay;

    @GetMapping
    public String trigger() throws InterruptedException {
        this.zafulGoodsInfoByDay.run();
        return "success";
    }

}
