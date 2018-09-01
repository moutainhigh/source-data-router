package com.globalegrow.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.globalegrow.ips.ServiceStart;
import com.globalegrow.ips.service.ProductCatalogHandlerService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceStart.class)
public class WarehouseTest {
	@Autowired
	private ProductCatalogHandlerService productCatalogHandlerService;

//	@org.junit.Test
	public void test() {
//		warehouseHandlerService.warehouseSaveRedis();
//		String str = SpringRedisUtil.getStringValue(IpsCatalogContant.WAREHOUSE_CODE_PREFIX + "SZWZTS");
//		System.out.println("+++++++++++++dfdf+++++++++++++"+str);
		productCatalogHandlerService.relShelfTimeForProduct();
	}
}




