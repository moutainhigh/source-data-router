package com.globalegrow.dyCubeBuildService.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 异常统一处理类
 * @author dengpizheng
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

//	@ExceptionHandler(value = Exception.class)
//	@ResponseBody
//	public Object exceptionHandler(HttpServletRequest req, Exception e) {
//		logger.error("系统异常：",e);
//		return null;
//	}
}
