package com.globalegrow.burylogcollectservice.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
@Component
public class SpringContextUtils implements ApplicationContextAware {
	
	 private static ApplicationContext   applicationContext;  
	   
	    /** 
	     * 实现ApplicationContextAware接口的回调方法，设置上下文环境 
	     */  
	    public void setApplicationContext(ApplicationContext applicationContext){  
	        SpringContextUtils.applicationContext = applicationContext;  
	    }  
	   
	    public static ApplicationContext getApplicationContext(){  
	        return applicationContext;  
	    }  
	   
	    /** 
	     * 获取对象 
	     * @return  Object 一个以所给名字注册的bean的实例 (service注解方式，自动生成以首字母小写的类名为bean name) 
	     */  
	    public static Object getBean(String name) throws BeansException{  
	        return applicationContext.getBean(name);  
	    }  

}
