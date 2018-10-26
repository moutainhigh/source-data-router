package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.UserActionDto;
import com.globalegrow.dy.dto.UserActionEsDto;
import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.globalegrow.util.JacksonUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.redisson.Redisson;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户行为接口，redis 实现
 * 只根据用户 device id 查询用户当天的行为数据
 */
@Service
public class RealTimeUserActionRedisServiceImpl implements RealTimeUserActionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("realTimeUserActionEsServiceImpl")
    private RealTimeUserActionService realTimeUserActionEsServiceImpl;

    @Value("${app.redis.readtime.prefix:dy_real_time_}")
    private String redisKeyPrefix;

    private RedissonClient redisson;

    @Value("${redis.type}")
    private String redisType;
    @Value("${redis.master:none}")
    private String master;
    @Value("${redis.nodes}")
    private String nodes;
    @Value("${redis.password}")
    private String redisPassword;
    @PostConstruct
    public void before() {
        Config config = new Config();
        if ("cluster".equals(redisType)) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            clusterServersConfig.addNodeAddress(nodes.split(","));
            clusterServersConfig.setPassword(redisPassword);
        } else if ("sentinel".equals(redisType)) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            sentinelServersConfig.setMasterName(master);
            sentinelServersConfig.addSentinelAddress(nodes.split(","));
            sentinelServersConfig.setPassword(redisPassword);
        }

        redisson = Redisson.create(config);
    }




    /**
     * 查询用户行为数据
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "queryFromEs")
    public UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException {
        long start = System.currentTimeMillis();
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        this.logger.debug("user_action_parameter: {}", userActionParameterDto);
        if (StringUtils.isNotEmpty(userActionParameterDto.getCookieId())) {
            List<UserActionDto> list = new ArrayList<>();
            String key = redisKeyPrefix + userActionParameterDto.getCookieId() + "_" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
            RSet<String> stringSet = redisson.getSet(key, StringCodec.INSTANCE);
            this.logger.debug("从 redis 查询到数据 key: {}, data: {}", key, stringSet);
            this.logger.info("cost_redis query from redis cost: {}", System.currentTimeMillis() - start);
            long handleStart = System.currentTimeMillis();
            stringSet.stream().map(s -> {
                try {
                    if (s.startsWith("\"")) {
                        s= s.replaceFirst("\"", "");
                    }
                    if (s.endsWith("\"")) {
                        s=s.substring(0, s.lastIndexOf("\""));
                    }
                    return JacksonUtil.readValue(s.replaceAll("\\\\", ""), UserActionEsDto.class);
                } catch (Exception e) {
                    logger.error("json 转换 error", e);
                    return null;
                }
            }).filter(d -> d!=null).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(UserActionEsDto :: getDevice_id))
                    .entrySet().stream().forEach(a -> {
                handleUserActionData(list, a, logger);
            });
            this.logger.info("cost_handle string convert cost: {}", System.currentTimeMillis() - handleStart);
            userActionResponseDto.setData(list);
        }

        return userActionResponseDto;
    }

    public UserActionResponseDto queryFromEs(UserActionParameterDto userActionParameterDto) throws IOException, ParseException {
        this.logger.info("redis 服务熔断，查询发送到 es");
        return this.realTimeUserActionEsServiceImpl.userActionData(userActionParameterDto);
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
