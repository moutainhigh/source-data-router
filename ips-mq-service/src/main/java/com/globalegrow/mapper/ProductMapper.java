package com.globalegrow.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.globalegrow.bean.ProductInfo;

@Mapper
public interface ProductMapper {

	void saveProductInfo(ProductInfo productInfo);

	ProductInfo getProductInfo(String sku);

	void updateProductInfo(ProductInfo productInfo);
}
