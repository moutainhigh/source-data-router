package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.UserActionDto;
import com.globalegrow.dy.dto.UserActionParameterDto;

import java.io.IOException;
import java.util.List;

public interface RealTimeUserActionService {

    /**
     * 查询用户行为数据
     * @param userActionParameterDto
     * @return
     */
    List<UserActionDto> userActionData(UserActionParameterDto userActionParameterDto) throws IOException;

}
