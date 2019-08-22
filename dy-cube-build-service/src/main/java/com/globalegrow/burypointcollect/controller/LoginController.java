package com.globalegrow.burypointcollect.controller;

import com.globalegrow.burypointcollect.utils.EncryptUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Controller
public class LoginController {
    
    @RequestMapping(value="login", method = { RequestMethod.GET, RequestMethod.POST },produces=MediaType.APPLICATION_JSON_VALUE) 
    @ResponseBody
    public String login(@RequestParam  String username,@RequestParam  String password,HttpServletResponse response)  {
    	JSONObject jsonObject = new JSONObject ();
    	password = EncryptUtil.sign(password, "MD5");
    	String usernameDefault = "KYLIN";
    	String passwordDefault = EncryptUtil.sign("KYLINADMIN", "MD5");
    	// 验证用户是否是登陆状态
        String token = UUID.randomUUID ().toString ().replace ("-", "");
        System.out.println("+++controller++token++"+token);
    	if(usernameDefault.equals(username)&&passwordDefault.equals(password)) {
    		System.out.println("+++33333333++token++"+token);
    		jsonObject.put ("status", "0");// 登陆成功
 	        Cookie cookie = new Cookie ("token", username + "_" + password + "_" + token);
 	        cookie.setMaxAge (600);
 	        cookie.setPath ("/");
 	        response.addCookie (cookie);
 	        return jsonObject.toJSONString ();
    	}else {
    		jsonObject.put ("status", "-1");// 登录不成功
    	    jsonObject.put ("msg", "用户名或密码错误！");
    	    return jsonObject.toJSONString ();
    	}
    }
    

}
