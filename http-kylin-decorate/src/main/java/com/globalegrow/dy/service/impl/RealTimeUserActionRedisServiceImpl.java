package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.UserActionDto;
import com.globalegrow.dy.dto.UserActionEsDto;
import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户行为接口，redis 实现
 * 只根据用户 device id 查询用户当天的行为数据
 */
@Service
public class RealTimeUserActionRedisServiceImpl implements RealTimeUserActionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 查询用户行为数据
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException {
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        this.logger.debug("user_action_parameter: {}", userActionParameterDto);
        if (StringUtils.isNotEmpty(userActionParameterDto.getCookieId())) {
            List<UserActionDto> list = new ArrayList<>();
            String key = "dy_real_time_" + userActionParameterDto.getCookieId() + "_" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
            Set<String> stringSet = SpringRedisUtil.SMEMBERS(key);
            this.logger.debug("从 redis 查询到数据 key: {}, data: {}", key, stringSet);
            stringSet.stream().map(s -> {
                try {
                    return JacksonUtil.readValue(s, UserActionEsDto.class);
                } catch (Exception e) {
                    logger.error("json 转换 error", e);
                    return null;
                }
            }).filter(d -> d!=null).collect(Collectors.toList()).parallelStream().collect(Collectors.groupingBy(UserActionEsDto :: getDevice_id))
                    .entrySet().parallelStream().forEach(a -> {
                handleUserActionData(list, a, logger);
            });
            userActionResponseDto.setData(list);
        }

        return userActionResponseDto;
    }

    static void handleUserActionData(List<UserActionDto> list, Map.Entry<String, List<UserActionEsDto>> a, Logger logger) {
        UserActionDto userActionDto = new UserActionDto();
        userActionDto.setCookieId(a.getKey());
        List<UserActionEsDto> data = a.getValue();
        if (data != null && data.size() > 0) {
            userActionDto.setUserId(data.get(0).getUser_id());
            data.stream().collect(Collectors.groupingBy(UserActionEsDto :: getEvent_name))
                    .entrySet().stream().forEach(e -> {
                try {
                    AppEventEnums.valueOf(e.getKey()).handleEventResult(userActionDto, e.getValue());
                } catch (IllegalArgumentException e1) {
                    logger.error("event not supported {}", e.getKey(), e);
                }
            });
        }
        list.add(userActionDto);
    }
}
