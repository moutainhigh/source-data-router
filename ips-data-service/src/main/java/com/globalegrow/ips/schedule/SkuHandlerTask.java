package com.globalegrow.ips.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.globalegrow.ips.service.SkuHandlerService;

@Service
@EnableScheduling
public class SkuHandlerTask {

	@Autowired
	private SkuHandlerService skuHandlerService;

	@Scheduled(cron = "${sku_spu.handler.cron}")
	public void skuTask() {
		skuHandlerService.skuSpuSaveRedis();
		
	}
	
}
