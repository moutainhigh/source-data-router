package com.globalegrow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"com.globalegrow"})
@EnableScheduling
@EnableKafka
public class ServiceStart {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStart.class, args);
    }

}