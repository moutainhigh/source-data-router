package com.globalegrow.ips.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.globalegrow.ips.bean.ProductBaseInfo;
import com.globalegrow.ips.constants.IpsCatalogContant;
import com.globalegrow.ips.mapper.dy.IpsProductMapper;
import com.globalegrow.ips.service.SkuHandlerService;
import com.globalegrow.util.SpringRedisUtil;

@Service
public class SkuHandlerServiceImpl implements SkuHandlerService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catalog.redis.expire.seconds}")
	private long expireSeconds;

	@Resource
	private IpsProductMapper ipsProductMapper;

	@Override
	public void skuSpuSaveRedis() {
		logger.info("dyTransactionManager beginnig!");
		int pstart = 0;
		int psize = 1000;
		boolean isContinue = false;
		do {
			isContinue = false;
			List<ProductBaseInfo> productBaseInfos = this.getProductBaseInfosServ(pstart, psize);
			if (productBaseInfos != null && !productBaseInfos.isEmpty()) {
				isContinue = true;
				for (ProductBaseInfo productBaseInfo : productBaseInfos) {
					try {
						String sku = productBaseInfo.getSku();
						String spu = productBaseInfo.getSpu();
						logger.info("skuSpuSaveRedis save" + sku);
						SpringRedisUtil.put(IpsCatalogContant.P_SKU_PREFIX + sku, spu);
					} catch (Exception e) {
						logger.error("skuSpuSaveRedis save fail!");
					}
				}
				pstart = pstart + psize;
			}
		} while (isContinue);

	}

	@Transactional(value = "dyTransactionManager", propagation = Propagation.REQUIRED)
	public List<ProductBaseInfo> getProductBaseInfosServ(int pstart, int psize) {
		return ipsProductMapper.getProductBaseInfos(pstart, psize);
	}
}
