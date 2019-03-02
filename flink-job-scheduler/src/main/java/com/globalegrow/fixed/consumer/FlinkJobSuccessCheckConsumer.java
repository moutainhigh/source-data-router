package com.globalegrow.fixed.consumer;

import com.globalegrow.fixed.queen.FlinkJobStatesCheck;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.DelayQueue;

@Data
@Slf4j
@RequiredArgsConstructor
public class FlinkJobSuccessCheckConsumer implements Runnable{

    @NonNull
    private DelayQueue<FlinkJobStatesCheck> flinkJobStatesChecks;

    private String flinkJobHistoryServer = "http://bts-master:8082/jobs/";

    @Override
    public void run() {
        try {
            FlinkJobStatesCheck statesCheck = this.flinkJobStatesChecks.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
