package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.service.RealTimeUserActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("user")
public class UserActionController {

    @Autowired
    @Qualifier("realTimeUserActionEsServiceImpl")
    private RealTimeUserActionService realTimeUserActionEsServiceImpl;

    @Autowired
    @Qualifier("realTimeUserActionRedisServiceImpl")
    private RealTimeUserActionService realTimeUserActionRedisServiceImpl;

    /**
     * 上线后删除
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "userActionResponseDto",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userAction(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException {
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        userActionResponseDto.setData(this.realTimeUserActionEsServiceImpl.userActionData(parameterDto));
        return userActionResponseDto;
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
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        userActionResponseDto.setData(this.realTimeUserActionEsServiceImpl.userActionData(parameterDto));
        return userActionResponseDto;
    }

    /**
     * redis 用户数据
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "getUserInfoRedis",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userActionInfoRedis(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException {
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        userActionResponseDto.setData(this.realTimeUserActionRedisServiceImpl.userActionData(parameterDto));
        return userActionResponseDto;
    }
}
