package com.globalegrow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.globalegrow"},exclude={DataSourceAutoConfiguration.class})
@EnableScheduling
@EnableKafka
public class ServiceStart {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStart.class, args);
    }

}
