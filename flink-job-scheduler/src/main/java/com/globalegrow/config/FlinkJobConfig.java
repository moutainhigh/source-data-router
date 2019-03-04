package com.globalegrow.config;

import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.fixed.queen.FlinkJobStatesCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Configuration
public class FlinkJobConfig {

    @Bean
    public DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens() {
        return new DelayQueue<>();
    }

    @Bean
    public DelayQueue<FlinkJobStatesCheck> flinkJobStatesChecks() {
        return new DelayQueue<>();
    }

    @Bean
    public LinkedBlockingDeque<FlinkBashJob> flinkBuryLogDataJobs(){
        return new LinkedBlockingDeque<>();
    }

    @Bean
    public Map<String, FlinkBashJob> currentBuryLogJobs() {
        return new ConcurrentHashMap<>();
    }

}
