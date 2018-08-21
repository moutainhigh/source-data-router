package com.globalegrow.ips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"com.globalegrow"})
public class ServiceStart {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ServiceStart.class, args);
	}
}
