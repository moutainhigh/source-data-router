package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.UserActionData;
import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SentinelServersConfig;
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

@Data
@Slf4j
@Service
public class RealTimeUserActionRedisServiceImpl implements RealTimeUserActionService {

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

    private String emptyEvent = "empty";

    @Value("${redis.fulfill.es:true}")
    private Boolean fulfillDataFromEs = true;

    @Value("${query-realtime-data-from-es:false}")
    private Boolean queryRealtimeDataFromEs = false;

    @Value("${redis.read-model:MASTER_SLAVE}")
    private String readModel;


    @PostConstruct
    public void before() {
        Config config = new Config();
        config.setCodec(new StringCodec());
        if ("cluster".equals(redisType)) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            clusterServersConfig.addNodeAddress(nodes.split(","));
            clusterServersConfig.setPassword(redisPassword);
            clusterServersConfig.setFailedSlaveReconnectionInterval(1000);
            if ("MASTER_SLAVE".equals(this.readModel)) {
                clusterServersConfig.setReadMode(ReadMode.MASTER_SLAVE);
            }
        } else if ("sentinel".equals(redisType)) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            sentinelServersConfig.setMasterName(master);
            sentinelServersConfig.addSentinelAddress(nodes.split(","));
            sentinelServersConfig.setPassword(redisPassword);
            if ("MASTER_SLAVE".equals(this.readModel)) {
                sentinelServersConfig.setReadMode(ReadMode.MASTER_SLAVE);
            }
        }

        this.redisson = Redisson.create(config);
    }


    /**
     * ????????????????????????
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException, ParseException {
        return null;
    }

    /**
     * ??? redis ?????????,redis ??????????????? es ??????
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto getActionByUserDeviceId(UserActionParameterDto userActionParameterDto) {
        //Long current = System.currentTimeMillis();
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
            log.debug("?????????????????? redis key : {}", id);
            RList<String> rList = this.redisson.getList(id);
            //this.logger.info("redis ????????????:{} ms", System.currentTimeMillis()-current);
            if (rList == null || rList.size() == 0 && this.fulfillDataFromEs) {
                // ??? es ??????????????????????????? es mark
                List<String> skus = this.realTimeUserActionEsServiceImpl.getById(userActionParameterDto.getCookieId() + eventName, site);
                if (skus != null && skus.size() > 0) {
                    skus.stream().forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));

                    // ?????? redis ????????? es ????????????
                    rList.addAllAsync(skus.stream().map(value -> value + searchWordSplitString).collect(Collectors.toList()));
                    // ??????????????????
                    rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);
                } else {

                    // ???????????????
                    rList.addAsync(this.emptyEvent);
                    rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);

                }

            } else {

                List<String> redisList = rList.readAll().stream().collect(Collectors.toList());

                redisList.stream().filter(value -> !emptyEvent.equals(value)).forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(handleEsMark(value.substring(value.lastIndexOf("_") + 1))))));
                log.debug("redis data {}", redisList);
                if (list.size() < userActionParameterDto.getSize()) {
                    //Long maxTime = list.stream().mapToLong(UserActionData::getTime).max().getAsLong();
                    // ???????????? es ????????? es
                    if (redisList.stream().filter(value -> value.endsWith(searchWordSplitString)).count() == 0
                            && redisList.stream().filter(value -> emptyEvent.equals(value)).count() == 0 && this.fulfillDataFromEs) {
                        log.debug("redis ?????????????????? {} ????????? es ????????????????????? {}", userActionParameterDto.getSize());
                        // set ??????
                        Set<String> history1000 = new HashSet<>();
                        List<String> skus = this.realTimeUserActionEsServiceImpl.getById(userActionParameterDto.getCookieId() + eventName, site);
                        if (skus != null && skus.size() > 0) {

                            history1000.addAll(skus);
                            history1000.addAll(redisList);

                            list.clear();

                            history1000.stream().forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));

                            rList.clear();
                            // ?????? es mark
                            rList.addAllAsync(history1000.stream().map(value -> value + searchWordSplitString).collect(Collectors.toList()));
                            rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);

                        } else {
                            // ?????? es mark
                            rList.clear();
                            // ?????? es mark
                            rList.addAllAsync(redisList.stream().map(value -> value + searchWordSplitString).collect(Collectors.toList()));
                            rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);
                            // rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);
                        }

                        if (list.size() == 0) {

                            // ???????????????
                            rList.addAsync(this.emptyEvent);
                            rList.expireAsync(this.redisExpireTime, TimeUnit.SECONDS);

                        }

                    }
                }
            }

            //Collections.sort(list);
            if (list.size() > userActionParameterDto.getSize()) {
                log.debug("???????????? {}", userActionParameterDto.getSize());
                data.put(eventName, list.stream().limit(userActionParameterDto.getSize()).collect(Collectors.toCollection(() -> new TreeSet<>())));
            } else {
                data.put(eventName, list);
            }


        });
        userActionResponseDto.setData(data);
        return userActionResponseDto;
    }

    /**
     * ????????? es ???????????????????????????
     *
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
