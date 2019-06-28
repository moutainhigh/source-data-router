package com.globalegrow.web;

import com.globalegrow.report.order.GbOrderInfoHandle;
import com.globalegrow.report.order.ZafulOrderInfoHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("cache")
@RestController
@Deprecated
public class OrderCacheConfigController {

    @Autowired
    private ZafulOrderInfoHandle zafulOrderInfoHandle;

    @Autowired
    private GbOrderInfoHandle gbOrderInfoHandle;

    @RequestMapping("zaful")
    public Object zafulOrderCacheSecondsConfig(Long seconds) {
        this.zafulOrderInfoHandle.setOrderCacheSeconds(seconds);
        return this.gbOrderInfoHandle.getOrderCacheSeconds();
    }

    @RequestMapping("gb")
    public Object gbOrderCacheSecondsConfig(Long seconds) {
        this.gbOrderInfoHandle.setOrderCacheSeconds(seconds);
        return this.gbOrderInfoHandle.getOrderCacheSeconds();
    }

}
