package com.globalegrow.service;

import java.util.List;

import com.globalegrow.bean.ProductInfo;

public interface ProductService {

	void saveProductInfoHandler(ProductInfo productInfo);

	void saveProductInfoHandler(List<ProductInfo> productInfos);
}
