package com.globalegrow.burylogcollectservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.globalegrow.burylogcollectservice.kafka.TestSender;
@EnableAspectJAutoProxy
@SpringBootApplication
public class ServiceStart {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ServiceStart.class, args);
//		ConfigurableApplicationContext context = SpringApplication.run(ServiceStart.class, args);
//		TestSender testSender = context.getBean(TestSender.class);		
//		testSender.sendLog();
	}
}
