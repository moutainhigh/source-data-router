package com.globalegrow.ips.mapper.pdm;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.globalegrow.ips.bean.ProductCatalog;

@Mapper
public interface ProductCatalogMapper {

	List<ProductCatalog> getProductCatalogs(@Param(value="pstart")int pstart, @Param(value="psize")int psize);
	
	ProductCatalog getProductCatalog( @Param(value="id")Long id);
}
