package com.globalegrow.ips.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.globalegrow.ips.bean.ProductCatalog;
import com.globalegrow.ips.constants.IpsCatalogContant;
import com.globalegrow.ips.mapper.pdm.ProductCatalogMapper;
import com.globalegrow.ips.service.ProductCatalogHandlerService;
import com.globalegrow.util.SpringRedisUtil;

@Service
public class ProductCatalogHandlerServiceImpl implements ProductCatalogHandlerService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catalog.redis.expire.seconds}")
	private long expireSeconds;

	@Resource
	private ProductCatalogMapper productCatalogMapper;

	@Override
	@Transactional(value = "pdmTransactionManager", propagation = Propagation.REQUIRED)
	public void pdmCatalogSaveRedis() {
		logger.info("pdmCatalogSaveRedis beginnig!");
		int pstart = 0;
		int psize = 100;
		boolean isContinue = false;
		do {
			isContinue = false;
			List<ProductCatalog> productCatalogs = productCatalogMapper.getProductCatalogs(pstart, psize);
			if (productCatalogs != null && !productCatalogs.isEmpty()) {
				isContinue = true;
				for (ProductCatalog productCatalog : productCatalogs) {
					try {
						Long catalogId = productCatalog.getCatalogId();
						SpringRedisUtil.put(IpsCatalogContant.P_CATALOG_ID_PREFIX + catalogId,
								productCatalog.getNameCh(), expireSeconds);
						String path = productCatalog.getPath();
						if (path == null) {
							continue;
						}
						String[] pathKeys = path.split(",");
						StringBuilder pathName = new StringBuilder();
						for (int i = 1; i < pathKeys.length; i++) {
							String ctlChName = SpringRedisUtil
									.getStringValue(IpsCatalogContant.P_CATALOG_ID_PREFIX + pathKeys[i]);
							if (StringUtils.isBlank(ctlChName)) {
								ProductCatalog pCatalog = productCatalogMapper
										.getProductCatalog(Long.valueOf(pathKeys[i]));
								if (pCatalog != null) {
									ctlChName = pCatalog.getNameCh();
									SpringRedisUtil.put(IpsCatalogContant.P_CATALOG_ID_PREFIX + pathKeys[i], ctlChName,
											expireSeconds);
								}
							}
							if (i != 1) {
								pathName.append(",");
							}
							pathName.append(ctlChName);
						}
						SpringRedisUtil.put(IpsCatalogContant.P_CATALOG_PATH_PREFIX + catalogId, pathName.toString(),
								expireSeconds);

					} catch (Exception e) {
						logger.error("pdmCatalogSaveRedis save fail!");
					}

				}
				pstart = pstart + psize;
			}
		} while (isContinue);

	}
}
