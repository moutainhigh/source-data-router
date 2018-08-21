package com.globalegrow.ips.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.globalegrow.ips.service.WarehouseHandlerService;

@Service
@EnableScheduling
public class WarehouseHandlerTask {

	@Autowired
	private WarehouseHandlerService warehouseHandlerService;

	@Scheduled(cron = "${warehouse.handler.cron}")
	public void warehouseTask() {
	
		warehouseHandlerService.warehouseSaveRedis();
		
	}
	
}
