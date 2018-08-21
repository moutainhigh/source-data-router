package com.globalegrow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.globalegrow"})
@MapperScan("com.globalegrow.dy.mapper")
public class ServiceStart {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStart.class, args);
    }

}
