package com.globalegrow.ips.mapper.pub;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.globalegrow.ips.bean.WebCategory;

@Mapper
public interface WebCatMapper {
	
	List<WebCategory> getWebCats(@Param(value="pstart") int pstart, @Param(value="psize") int psize);
}
