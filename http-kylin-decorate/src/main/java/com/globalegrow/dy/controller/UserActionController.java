package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.service.RealTimeUserActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("user")
public class UserActionController {

    @Autowired
    private RealTimeUserActionService realTimeUserActionEsServiceImpl;

    @RequestMapping(value = "userActionResponseDto",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userAction(@Validated UserActionParameterDto parameterDto) throws IOException {
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        userActionResponseDto.setData(this.realTimeUserActionEsServiceImpl.userActionData(parameterDto));
        return userActionResponseDto;
    }

}
