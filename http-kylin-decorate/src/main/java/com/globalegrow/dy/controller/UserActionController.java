package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.globalegrow.util.SpringRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("user")
public class UserActionController {

    @Autowired
    @Qualifier("realTimeUserActionEsServiceImpl")
    private RealTimeUserActionService realTimeUserActionEsServiceImpl;

    @Autowired
    @Qualifier("realTimeUserActionRedisServiceImpl")
    private RealTimeUserActionService realTimeUserActionRedisServiceImpl;

 /*   @RequestMapping("redis-test")
    public Set<String> getData(String key) {
        return SpringRedisUtil.SMEMBERS(key);
    }

    @RequestMapping("redis-test-1")
    public String getData1(String key) {
        return SpringRedisUtil.getStringValue(key);
    }

    @RequestMapping("redis-test-2")
    public Set<String> getData2(String key) {
        return SpringRedisUtil.getAllKeyByPrefix(key);
    }*/

    /**
     * 上线后删除
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "userActionResponseDto",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userAction(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException {
        return this.realTimeUserActionEsServiceImpl.userActionData(parameterDto);
    }

    //

    /**
     * 与源文档保持一致
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "getUserInfo",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userActionInfo(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException {
        return this.realTimeUserActionEsServiceImpl.userActionData(parameterDto);
    }

    /**
     * redis 用户数据
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "getUserInfoRedis",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userActionInfoRedis(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException {
        return this.realTimeUserActionRedisServiceImpl.userActionData(parameterDto);
    }
}
