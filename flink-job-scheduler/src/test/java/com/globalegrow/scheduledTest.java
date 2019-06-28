package com.globalegrow;

import org.junit.Test;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Auther: joker
 * @Date: 2019/6/28 11:44
 * @Description:
 */
public class scheduledTest {
    @Test
    @Scheduled(fixedDelay = 1000)
    public void scheduleTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println("-----------" + i);
        }
    }

}
