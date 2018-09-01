package com.globalegrow.ips.mapper.dy;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.globalegrow.ips.bean.ProductInfo;

@Mapper
public interface ProductMapper {

	List<ProductInfo> getProductInfos(String sku);

	void updateProductInfo(@Param(value = "id") Long id,@Param(value = "shelfTime") String shelfTime);

	void deleteProductInfoById(@Param(value = "id") Long id);
}
