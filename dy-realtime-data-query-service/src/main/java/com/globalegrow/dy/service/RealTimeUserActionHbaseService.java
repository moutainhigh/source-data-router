package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;

import java.io.IOException;
import java.text.ParseException;

/**
 * @Description 实时推荐用户行为服务
 * @Author chongzi
 * @Date 2018/11/26 11:32
 * @Param 
 * @return 
 **/
public interface RealTimeUserActionHbaseService {

    /**
     * 从hbase查询用户行为数据
     * @param userActionParameterDto
     * @return
     */
    UserActionResponseDto getUserActionDataFromHbase(UserActionParameterDto userActionParameterDto);

}
