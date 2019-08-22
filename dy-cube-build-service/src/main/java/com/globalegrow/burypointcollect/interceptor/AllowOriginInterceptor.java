package com.globalegrow.burypointcollect.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AllowOriginInterceptor extends HandlerInterceptorAdapter {
	/**
	 * 允许部分接口跨域访问
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
//		if (request.getRequestURI().contains("wechat-propagate-back/ccv/saveofnotifyccvs")||
//			request.getRequestURI().contains("wechat-propagate-back/ccv/getccvs")||
//			request.getRequestURI().contains("wechat-propagate-back/ccv/getccvslist")) {
			response.setHeader("Access-Control-Allow-Origin", "*");
			System.out.println("++++++++Access-Control-Allow-Origin+++++++++++");
//		}
		return true;
	}
}
