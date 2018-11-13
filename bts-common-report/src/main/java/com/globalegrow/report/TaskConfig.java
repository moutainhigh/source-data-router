package com.globalegrow.report;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Configuration
public class TaskConfig {

    /*@Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(40);
        return threadPoolTaskExecutor;
    }

    @Bean
    public Map<String, Thread> reportThreads() {
        return new ConcurrentHashMap<>();
    }*/

    @Bean
    public Map<String, ExecutorService> executorServiceMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, ReportExecutorService> reportExecutorServiceMap() {
        return new ConcurrentHashMap<>();
    }

}
