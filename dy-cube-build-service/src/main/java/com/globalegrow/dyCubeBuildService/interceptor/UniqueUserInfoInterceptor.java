package com.globalegrow.dyCubeBuildService.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UniqueUserInfoInterceptor implements HandlerInterceptor {

//	private static Logger LOGGER = LoggerFactory.getLogger(UniqueUserInfoInterceptor.class);
	
	

@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
    Cookie[] cookies = request.getCookies ();
    if (null != cookies) {
        for (Cookie cookie : cookies) {
            if ("token".equals (cookie.getName ())) {
                String username = cookie.getValue ().split ("_")[0];
                String password = cookie.getValue ().split ("_")[1];
                String token = cookie.getValue ().split ("_")[2];
                System.out.println("+++intercepter++token++"+token+username+password);
                if ("KYLIN".equals(username) && "KYLINADMIN".equals(password) && token!=null) {
                	System.out.println("+++intercepter++token++"+token);
                    return true;
                }
            }
        }
    }
    response.sendError (HttpServletResponse.SC_FORBIDDEN);
	System.out.println("++++++++UniqueUserInfoInterceptor++++++++");
    return false;
}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {

	}

}
