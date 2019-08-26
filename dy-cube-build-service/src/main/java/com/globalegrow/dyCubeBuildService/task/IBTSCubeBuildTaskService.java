package com.globalegrow.dyCubeBuildService.task;

import com.globalegrow.dyCubeBuildService.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public interface IBTSCubeBuildTaskService {

	void task();

	/**
	 * 自动增量构建cube
	 * 
	 * @param buildUrl      构建地址
	 * @param authorization 验证信息 用户名:密码的base64
	 * @param cls
	 */
	static void cubeBuild(String cubeNameAndIsOpens, String buildUrl, String authorization, Class<?> cls) {
		Logger logger = LoggerFactory.getLogger(cls);
		
		if(StringUtils.isNotBlank(cubeNameAndIsOpens)) {
			RestTemplate restTemplate = (RestTemplate) SpringContextUtils.getBean("restTemplate");
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			requestHeaders.add("Authorization", "Basic " + authorization);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sourceOffsetStart", 0);
			params.put("sourceOffsetEnd", 9223372036854775807L);
			params.put("buildType", "BUILD");
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, requestHeaders);
			String[] cubeNameAndIsOpenArr = cubeNameAndIsOpens.split(",");
			for(String cubeNameAndIsOpen:cubeNameAndIsOpenArr) {
				String[] invokeCubeInfo = cubeNameAndIsOpen.split("\\|");
				if(Boolean.valueOf(invokeCubeInfo[1])) {
					try {
						String url = buildUrl+"/"+invokeCubeInfo[0];
						logger.info("cubeBuild,url:{}",url);
						restTemplate.exchange(url + "/build2", HttpMethod.PUT, requestEntity, String.class);
					} catch (Exception e) {
						logger.error("IBTSCubeBuildTaskService.cubeBuild() Exception.", e);
					}
				}
			}
		}	
		
		
		
		
		
	}
}
