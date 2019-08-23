package com.globalegrow.dyCubeBuildService;

import com.globalegrow.dyCubeBuildService.task.impl.MakeSureCreateTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan( basePackages = "com.globalegrow.*")
public class ServiceStart {
	
	public static void main(String[] args) throws Exception {
//		SpringApplication.run(ServiceStart.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(ServiceStart.class, args);
		MakeSureCreateTask makeSureCreateTask = context.getBean(MakeSureCreateTask.class);
		makeSureCreateTask.makeSureCreateTaskHandler();
	}
}
