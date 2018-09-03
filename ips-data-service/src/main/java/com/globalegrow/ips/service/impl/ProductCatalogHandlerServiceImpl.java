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

import com.globalegrow.ips.bean.Product;
import com.globalegrow.ips.bean.ProductCatalog;
import com.globalegrow.ips.bean.ProductInfo;
import com.globalegrow.ips.constants.IpsCatalogContant;
import com.globalegrow.ips.mapper.dy.ProductMapper;
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

	@Resource
	private ProductMapper productMapper;

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
								productCatalog.getNameCh());
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
									SpringRedisUtil.put(IpsCatalogContant.P_CATALOG_ID_PREFIX + pathKeys[i], ctlChName);
								}
							}
							if (i != 1) {
								pathName.append(",");
							}
							pathName.append(ctlChName);
						}
						SpringRedisUtil.put(IpsCatalogContant.P_CATALOG_PATH_PREFIX + catalogId, pathName.toString());

					} catch (Exception e) {
						logger.error("pdmCatalogSaveRedis save fail!");
					}

				}
				pstart = pstart + psize;
			}
		} while (isContinue);

	}

	@Override
	public void relShelfTimeForProduct() {
		logger.info("relShelfTimeForProduct beginnig!");
		int pstart = 0;
		int psize = 1000;
		boolean isContinue = false;
		do {
			isContinue = false;
			List<Product> products = this.getProducts(pstart, psize);
			if (products != null && !products.isEmpty()) {
				isContinue = true;
				for (Product product : products) {
					try {
						String sku = product.getGoodsSn();
						String shelfTime = product.getShelfTime();
						if (shelfTime != null) {
							shelfTime = shelfTime.substring(0, shelfTime.indexOf("."));
						}
						this.relShelfTimeForProductHandler(sku, shelfTime);
					} catch (Exception e) {
						logger.error("relShelfTimeForProduct save fail!");
					}

				}
				pstart = pstart + psize;
			}
		} while (isContinue);

	}

	@Transactional(value = "dyTransactionManager", propagation = Propagation.REQUIRED)
	public void relShelfTimeForProductHandler(String sku, String shelfTime) {
		List<ProductInfo> pInfos = productMapper.getProductInfos(sku);
		if (pInfos != null && !pInfos.isEmpty()) {
			if (pInfos.size() > 1) {
				for (int i = 0; i < pInfos.size() - 1; i++) {
					productMapper.deleteProductInfoById(pInfos.get(i).getId());
				}
			}
			if (StringUtils.isBlank(pInfos.get(pInfos.size() - 1).getShelfTime())) {
				System.out.println(sku+":"+ shelfTime);
				logger.info(sku+":"+ shelfTime);
				productMapper.updateProductInfo(pInfos.get(pInfos.size() - 1).getId(), shelfTime);
			}
		}

	}

	@Transactional(value = "pdmTransactionManager", propagation = Propagation.REQUIRED)
	public List<Product> getProducts(int pstart, int psize) {
		return productCatalogMapper.getProducts(pstart, psize);
	}
}
