package com.globalegrow.fixed.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 天池推荐报表
 */
@Component
public class ZafulRecommendReport extends AbstractFlinkJobSerialScheduler {


    @Scheduled(fixedDelay = 10000)
    @Override
    void run() throws InterruptedException {

        this.runFlinkJob();

    }


}
