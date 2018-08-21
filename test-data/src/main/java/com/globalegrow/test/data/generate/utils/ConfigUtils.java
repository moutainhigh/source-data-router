package com.globalegrow.test.data.generate.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/*@Configuration
@PropertySource(value="${service.config.file}",ignoreResourceNotFound = true)
@Component*/
public class ConfigUtils {
	@Autowired
    private Environment env;
	public  String getPropertyValue(String key) {
		return env.getProperty(key);
	}	
}
