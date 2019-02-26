package com.globalegrow.config;

import com.globalegrow.fixed.queen.AbstractFlinkJobQueen;
import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

@Configuration
public class DelayQueensConfig {

    @Bean
    public DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens() {
        return new DelayQueue<>();
    }

}
