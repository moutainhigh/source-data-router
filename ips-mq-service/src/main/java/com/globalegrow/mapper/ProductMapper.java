package com.globalegrow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.globalegrow.bean.ProductInfo;

@Mapper
public interface ProductMapper {

	void saveProductInfo(ProductInfo productInfo);

	ProductInfo getProductInfo(String sku);

	void updateProductInfo(ProductInfo productInfo);
	
	int insertProductInfoBatch(List<ProductInfo> productInfos);
}
