package com.globalegrow.cu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"com.globalegrow"})
@EnableKafka
public class ServiceStart {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStart.class, args);
    }

}