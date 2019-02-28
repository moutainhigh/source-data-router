package com.globalegrow.config;

import com.globalegrow.fixed.queen.AbstractFlinkJobQueen;
import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import com.globalegrow.fixed.queen.FlinkJobStatesCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

@Configuration
public class DelayQueensConfig {

    @Bean
    public DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens() {
        return new DelayQueue<>();
    }

    @Bean
    public DelayQueue<FlinkJobStatesCheck> flinkJobStatesChecks() {
        return new DelayQueue<>();
    }

}
