package com.globalegrow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.globalegrow.bean.ProductInfo;
import com.globalegrow.mapper.ProductMapper;
import com.globalegrow.service.ProductService;
import com.globalegrow.util.SpringRedisUtil;

@Service
public class ProductServiceImpl implements ProductService {

	public static final String P_SKU_PREFIX = "p_sku";

	@Autowired
	private ProductMapper productMapper;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveProductInfoHandler(ProductInfo productInfo) {
		String sku = productInfo.getGoods_sn();
		SpringRedisUtil.put(P_SKU_PREFIX + sku, productInfo.getProduct_sn());
		List<ProductInfo> pInfos = productMapper.getProductInfo(sku);
		if (pInfos != null && !pInfos.isEmpty()) {
			if (pInfos.size() > 1) {
				for (int i = 0; i < pInfos.size(); i++) {
					productMapper.deleteProductInfoById(pInfos.get(i).getId());
				}
				productMapper.saveProductInfo(productInfo);
			} else {
				productInfo.setId(pInfos.get(0).getId());
				productMapper.updateProductInfo(productInfo);
			}
		} else {
			productMapper.saveProductInfo(productInfo);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveProductInfoHandler(List<ProductInfo> productInfos) {
		productMapper.insertProductInfoBatch(productInfos);
	}

}
