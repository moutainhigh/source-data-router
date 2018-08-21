package com.globalegrow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.globalegrow.bean.ProductInfo;
import com.globalegrow.mapper.ProductMapper;
import com.globalegrow.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductMapper productMapper;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveProductInfoHandler(ProductInfo productInfo) {
		String sku = productInfo.getGoods_sn();
		ProductInfo pInfo = productMapper.getProductInfo(sku);
		if (pInfo != null) {
			productInfo.setId(pInfo.getId());
			productMapper.updateProductInfo(productInfo);
		} else {
			productMapper.saveProductInfo(productInfo);
		}
	}

}
