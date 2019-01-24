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

    @Value("${redis.key-prefix:dy_&&_app}")
    private String redisKeyPrefix;

    @Value("${redis.expire-seconds:86400}")
    private Long redisExpireTime;

    private RedissonClient redisson;

    private static final String searchWordSplitString = "\\u0001ES";

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
        Long current = System.currentTimeMillis();
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        Map<String, Set<UserActionData>> data = new HashMap<>();
        List<String> inputType = userActionParameterDto.getType();
        if (inputType == null) {
            inputType = new ArrayList<>();
        }
        if (inputType.size() < 1) {
            inputType.addAll(Arrays.stream(AppEventEnums.values()).map(AppEventEnums::name).collect(Collectors.toList()));
        }
        String site = userActionParameterDto.getSite().toLowerCase();
        inputType.parallelStream().forEach(eventName -> {

            Set<UserActionData> list = new TreeSet<>();
            String id = this.redisKeyPrefix.replaceFirst("&&", site) + userActionParameterDto.getCookieId() + eventName;
            this.logger.debug("用户实时数据 redis key : {}", id);
            RList<String> rList = this.redisson.getList(id);
            if (rList == null || rList.size() == 0) {
                // 从 es 查询，并将数据添加 es mark
                List<String> skus = this.realTimeUserActionEsServiceImpl.getById(userActionParameterDto.getCookieId() + eventName, site);
                if (skus != null) {
                    skus.stream().forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));

                    // 放入 redis 并添加 es 查询标签
                    rList.addAllAsync(skus.stream().map(value -> value + searchWordSplitString).collect(Collectors.toList()));
                    // 设置过期时间
                    rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);
                }

            } else {

                List<String> redisList = rList.readAll();

                redisList.stream().forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(handleEsMark(value.substring(value.lastIndexOf("_") + 1))))));

                if (list.size() < userActionParameterDto.getSize()) {
                    Long maxTime = list.stream().mapToLong(UserActionData::getTime).max().getAsLong();
                    // 最大数据时间戳少于 60 ms 为新数据，新数据少于 1000, 且未查询过 es
                    if (/*(maxTime - current) < 60 && */redisList.stream().filter(value -> value.endsWith(this.searchWordSplitString)).count() == 0) {
                        this.logger.debug("redis 中的数据少于 {} 条，从 es 中查询历史数据", userActionParameterDto.getSize());
                        // set 去重
                        Set<String> history1000 = new HashSet<>();
                        List<String> skus = this.realTimeUserActionEsServiceImpl.getById(userActionParameterDto.getCookieId() + eventName, site);
                        if (skus != null && skus.size() > 0) {

                            history1000.addAll(skus);
                            history1000.addAll(redisList);

                            list.clear();

                            history1000.stream().forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));

                            rList.clear();
                            // 添加 es mark
                            rList.addAllAsync(history1000.stream().map(value -> value + searchWordSplitString).collect(Collectors.toList()));
                            rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);

                        }

                    }
                }
            }

            //Collections.sort(list);
            if (list.size() > userActionParameterDto.getSize()) {
                data.put(eventName, list.stream().limit(userActionParameterDto.getSize()).collect(Collectors.toSet()));
            }else{
                data.put(eventName, list);
            }


        });
        userActionResponseDto.setData(data);
        return userActionResponseDto;
    }

    /**
     * 处理从 es 查询出来的标记数据
     * @param esMarked
     * @return
     */
    static String handleEsMark(String esMarked) {
        return esMarked.replaceAll("\\\\u0001ES", "");
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
