package com.globalegrow.ips.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.globalegrow.ips.bean.CdpWarehouseRet;
import com.globalegrow.ips.bean.WarehouseInfo;
import com.globalegrow.ips.constants.IpsCatalogContant;
import com.globalegrow.ips.rpc.WarehouseApiService;
import com.globalegrow.ips.service.WarehouseHandlerService;
import com.globalegrow.util.SpringRedisUtil;

@Service
public class WarehouseHandlerServiceImpl implements WarehouseHandlerService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catalog.redis.expire.seconds}")
	private long expireSeconds;

	@Resource
	private WarehouseApiService warehouseApiService;

	@Override
	public void warehouseSaveRedis() {
		logger.info("warehouseSaveRedis beginnig!");
		int page = 1;
		int per_page = 100;
		boolean isContinue = false;
		do {
			isContinue = false;
			CdpWarehouseRet cdpWarehouseRet = warehouseApiService.getWarehouseInfos(page, per_page);
			if (cdpWarehouseRet != null) {
				logger.info("cdpWarehouseRet not null");
				List<WarehouseInfo> warehouseInfos = cdpWarehouseRet.getList();
				for (WarehouseInfo warehouseInfo : warehouseInfos) {
					try {
						String warehouseSn = warehouseInfo.getWarehouse_sn();
						String warehouseName = warehouseInfo.getWarehouse_name();
						logger.info("warehouseSaveRedis save" + warehouseSn);
						SpringRedisUtil.put(IpsCatalogContant.WAREHOUSE_CODE_PREFIX + warehouseSn, warehouseName,
								expireSeconds);
					} catch (Exception e) {
						logger.error("warehouseSaveRedis save fail!");
					}
				}
				String total = cdpWarehouseRet.getTotal();
				if (total != null) {
					int totalNum = Integer.valueOf(total);
					if (totalNum >= page * per_page + 1) {
						isContinue = true;
						page++;
					}
				}
			}
		} while (isContinue);

	}
}
