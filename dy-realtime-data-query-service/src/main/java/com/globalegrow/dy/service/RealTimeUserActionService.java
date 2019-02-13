package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface RealTimeUserActionService {

    /**
     * 查询用户行为数据
     * @param userActionParameterDto
     * @return
     */
    UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException, ParseException;

    UserActionResponseDto getActionByUserDeviceId(UserActionParameterDto userActionParameterDto);

    UserActionResponseDto mock(UserActionParameterDto userActionParameterDto);

    List<String> getById(String id, String site);

}
