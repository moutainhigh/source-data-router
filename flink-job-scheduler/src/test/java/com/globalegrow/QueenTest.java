package com.globalegrow;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingDeque;

public class QueenTest {


    LinkedBlockingDeque<Integer> flinkBuryLogDataJobs = new LinkedBlockingDeque<>();

    @Test
    public void test() throws InterruptedException {
        flinkBuryLogDataJobs.offer(1);
        System.out.println(flinkBuryLogDataJobs.take());
        System.out.println(flinkBuryLogDataJobs.size());
    }

}
