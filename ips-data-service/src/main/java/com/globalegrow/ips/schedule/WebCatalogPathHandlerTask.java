package com.globalegrow.ips.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.globalegrow.ips.service.WebCatalogHandlerService;

@Service
@EnableScheduling
public class WebCatalogPathHandlerTask {

	@Autowired
	private WebCatalogHandlerService webCatalogHandlerService;
	
	@Scheduled(cron = "${web.catalog.single.handler.cron}")
	public void singleWebTask() {
		webCatalogHandlerService.singleWebCatalogNameSaveRedis();
		
	}
	
	@Scheduled(cron = "${web.catalog.handler.cron}")
	public void webTask() {
		webCatalogHandlerService.webCatalogNameSaveRedis();
		
	}
}
