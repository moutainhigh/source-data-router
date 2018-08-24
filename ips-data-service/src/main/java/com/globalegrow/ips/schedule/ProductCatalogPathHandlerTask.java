package com.globalegrow.ips.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.globalegrow.ips.service.ProductCatalogHandlerService;

@Service
@EnableScheduling
public class ProductCatalogPathHandlerTask {

	@Autowired
	private ProductCatalogHandlerService productCatalogHandlerService;

	@Scheduled(cron = "${pdm.catalog.path.handler.cron}")
	public void pdmTask() {
		productCatalogHandlerService.pdmCatalogSaveRedis();
		
	}
	
}
