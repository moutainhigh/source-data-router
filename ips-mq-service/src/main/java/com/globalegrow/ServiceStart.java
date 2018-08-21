package com.globalegrow;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = { "com.globalegrow" })
@EnableRabbit
public class ServiceStart {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ServiceStart.class, args);
	}
}
