package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.UserActionData;
import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionService;
import org.redisson.Redisson;
import org.redisson.api.RList;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        config.setCodec(new StringCodec());
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

        this.redisson = Redisson.create(config);
    }


    /**
     * 查询用户行为数据
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException, ParseException {
        return null;
    }

    /**
     * 从 redis 中查询,redis 中没有则从 es 查询
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto getActionByUserDeviceId(UserActionParameterDto userActionParameterDto) {
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        Map<String, List<UserActionData>> data = new HashMap<>();
        List<String> inputType = userActionParameterDto.getType();
        if (inputType == null) {
            inputType = new ArrayList<>();
        }
        if (inputType.size() < 1) {
            inputType.addAll(Arrays.stream(AppEventEnums.values()).map(AppEventEnums::name).collect(Collectors.toList()));
        }
        String site = userActionParameterDto.getSite().toLowerCase();
        inputType.parallelStream().forEach(eventName -> {

            List<UserActionData> list = new ArrayList<>();
            String id = "dy" + site + "apd" + userActionParameterDto.getCookieId() + eventName;
            this.logger.debug("用户实时数据 redis key : {}", id);
            RList<String> rList = this.redisson.getList(id);
            if (rList == null || rList.size() == 0) {
                // 从 es 查询
                List<String> skus = this.realTimeUserActionEsServiceImpl.getById(userActionParameterDto.getCookieId() + eventName, site);
                if (skus != null) {
                    skus.stream().forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));
                    rList.addAll(skus);
                    // 过期时间为 3 天
                    rList.expire(259200, TimeUnit.SECONDS);
                }

            }else {
                rList.readAll().stream().forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));
            }


            data.put(eventName, list);


        });
        userActionResponseDto.setData(data);
        return userActionResponseDto;
    }

    @Override
    public UserActionResponseDto mock(UserActionParameterDto userActionParameterDto) {
        return null;
    }

    @Override
    public List<String> getById(String id, String site) {
        return null;
    }
}
