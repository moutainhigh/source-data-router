package com.globalegrow.fixed.scheduler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每天将 gb app 用户行为中的 点击、加购、加收藏、下单、支付 事件初始化至 es 索引中
 * 数据来源，大数据 app 埋点
 */
@Slf4j
@Component
@Data
public class GbAppUserEventByDay extends AbstractFlinkJobSerialScheduler{


    private String flinkCommandLine = "";

    /**
     * 每天 3:10 运行
     */
    @Scheduled(cron = "${app.cron.zaful-goods-info:0 10 3 * * ?}")
    public void run(){

    }

}
