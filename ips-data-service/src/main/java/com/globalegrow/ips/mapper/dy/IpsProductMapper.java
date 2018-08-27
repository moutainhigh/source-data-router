package com.globalegrow.ips.mapper.dy;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.globalegrow.ips.bean.ProductBaseInfo;

@Mapper
public interface IpsProductMapper {
	
	List<ProductBaseInfo> getProductBaseInfos(@Param(value="pstart") int pstart, @Param(value="psize") int psize);
}
