package com.globalegrow.dyCubeBuildService.service;

public class LoginService {

	public String login(String username, String password) {
//	    JSONObject jsonObject = new JSONObject ();
	    // 验证用户名密码是否正确
//	    LOGGER.info ("用户登陆明文密码{}", password);
//	    password = MD5.getMD5ofStr (password);
//	    LOGGER.info ("用户登陆MD5加密密码{}", password);
//	    List<CgpMngUser> cgpMngUserList = cgpMngUserService.findByUserCodeAndUserPassword (username, password);//查用户表
//	    if (cgpMngUserList != null && cgpMngUserList.size () != 0) {
//	        CgpMngUser cgpMngUser = cgpMngUserList.get (0);
//	             
//	        // 验证用户是否是登陆状态
//	        String token = UUID.randomUUID ().toString ().replace ("-", "");
//	        List<CgpLoginUserCache> cgpLoginUserCacheList = cgpMngUserService.findLoginUserCacheByUserCodeAndUserPassword (username, password);//查登陆用户缓存表
//	        if (cgpLoginUserCacheList != null && cgpLoginUserCacheList.size () != 0) {
//	            cgpMngUserService.updateLoginUserToken (cgpLoginUserCacheList.get (0).getClcPid (), token);//更新登陆用户的token，这样前面登陆的用户再次请求，发现令牌不一致就会拒绝访问啦（判断逻辑写在拦截器，这部分逻辑我也会在文章下面给出）
//	            jsonObject.put ("status", "0000");// 二次登陆
//	            jsonObject.put ("user", cgpMngUser);
//	            Cookie cookie = new Cookie ("token", username + "_" + password + "_" + token);
//	            cookie.setMaxAge (Integer.MAX_VALUE);
//	            cookie.setPath ("/");
//	            response.addCookie (cookie);
//	            LOGGER.info ("登陆成功============该用户已在其他地方登陆中或上次登陆是非正常登出");
//	            return jsonObject.toJSONString ();
//	        }
//	        cgpMngUserService.insertLoginUserCache (cgpMngUser.getUserCode (), cgpMngUser.getUserPassword (), token);
//	        jsonObject.put ("status", "0000");// 初次登陆
//	        jsonObject.put ("user", cgpMngUser);
//	        Cookie cookie = new Cookie ("token", username + "_" + password + "_" + token);
//	        cookie.setMaxAge (Integer.MAX_VALUE);
//	        cookie.setPath ("/");
//	        response.addCookie (cookie);
//	        LOGGER.info ("登陆成功============该用户是初次登陆");
//	        return jsonObject.toJSONString ();
//	    }
//	    jsonObject.put ("status", "0001");// 登录不成功
//	    jsonObject.put ("msg", "用户名或密码错误！");
//	    LOGGER.info ("登陆失败============用户名或密码有误");
//	    return jsonObject.toJSONString ();
		return null;
	}

}
