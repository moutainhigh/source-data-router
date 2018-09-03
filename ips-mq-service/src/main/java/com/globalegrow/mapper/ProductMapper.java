package com.globalegrow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.globalegrow.bean.ProductInfo;

@Mapper
public interface ProductMapper {

	void saveProductInfo(ProductInfo productInfo);

	List<ProductInfo> getProductInfo(String sku);

	void updateProductInfo(ProductInfo productInfo);

	int insertProductInfoBatch(List<ProductInfo> productInfos);

	void deleteProductInfoById(@Param(value = "id") Long id);
}
