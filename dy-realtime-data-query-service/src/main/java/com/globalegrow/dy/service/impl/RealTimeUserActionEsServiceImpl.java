package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import io.searchbox.core.Search;
import io.searchbox.params.Parameters;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
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
import java.util.stream.Collectors;

@Service
public class RealTimeUserActionEsServiceImpl implements RealTimeUserActionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    //@Qualifier("myJestClient")
    private JestClient jestClient;
    /**
     * 索引前缀，当天数据只查询当天索引 dy-app-data-temp-
     */
    @Value("${app.es.index-prefix}")
    private String indexPrefix;
    /**
     * 索引别名，从全部数据中搜索
     */
    @Value("${app.es.index-aliases}")
    private String indexAliases;

    @Value("${app.es.index-type:log}")
    private String indexType;

    @Value("${app.es.scroll:1s}")
    private String scroll;


    private String appIndexPrefix = "dy_app_&&_event";

    private String appRealtimeEventIndex = "dy_app_&&_event_realtime";

    @PostConstruct
    public void before() {

    }

    /**
     * 从 es 查询用户行为数据
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException, ParseException {
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
        String esIndex = this.appIndexPrefix.replace("&&", site);
        inputType.parallelStream().forEach(eventName -> {

            JestResult result = null;

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // 查询单个用户行为数据
            QueryBuilder qb = QueryBuilders.termQuery("device_id.keyword", userActionParameterDto.getCookieId());
            queryBuilder.filter(qb);

            // 时间限制
            /*if (userActionParameterDto.getStartDate() != null && userActionParameterDto.getEndDate() != null) {
                QueryBuilder qbTime = QueryBuilders.rangeQuery("timestamp").gte(userActionParameterDto.getStartDate())
                        .lte(userActionParameterDto.getEndDate()).includeLower(false).includeUpper(false).boost(2.0F);
                queryBuilder.filter(qbTime);
            }*/


            SortBuilder sortBuilder = new FieldSortBuilder("timestamp");
            sortBuilder.order(SortOrder.DESC);

            searchSourceBuilder.from(0);
            searchSourceBuilder.size(userActionParameterDto.getSize());

            searchSourceBuilder.query(queryBuilder);
            searchSourceBuilder.sort(sortBuilder);
            Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
            this.logger.debug("elasticsearch 搜索条件: {}", searchSourceBuilder.toString());
            builder.addIndex(esIndex + "-" + eventName);
            Search search = builder
                    .addType("log").setParameter(Parameters.ROUTING, userActionParameterDto.getCookieId())
                    .build();

            try {
                result = jestClient.execute(search);
            } catch (IOException e) {
                logger.error("query es error ,params: {}", searchSourceBuilder.toString(), e);
            }
            //logger.info("query from es cost: {}", System.currentTimeMillis() - start);


            if (result != null) {
                long handleStart = System.currentTimeMillis();
                //logger.debug("es data result size: {}", result.getSourceAsObjectList(UserActionEsDto.class).size());
                result.getSourceAsObjectList(UserActionEsDto.class).stream().collect(Collectors.groupingBy(UserActionEsDto::getEvent_name)).entrySet().stream().forEach(e -> {
                    data.put(e.getKey(), e.getValue().stream().map(esd -> new UserActionData(esd.getEvent_value(), esd.getTimestamp())).collect(Collectors.toSet()));
                });
                //logger.debug("handle result costs: {}", System.currentTimeMillis() - handleStart);
            }
        });

        userActionResponseDto.setSize(userActionParameterDto.getSize());
        userActionResponseDto.setData(data);

        return userActionResponseDto;
    }

    /**
     * 从 es 获取最近 1000 条事件数据
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto getActionByUserDeviceId(UserActionParameterDto userActionParameterDto) {
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
        String esIndex = this.appRealtimeEventIndex.replace("&&", site);

        inputType.parallelStream().forEach(eventName -> {

            String id = userActionParameterDto.getCookieId() + eventName;

            Get get = new Get.Builder(esIndex, id).type("log").setParameter(Parameters.ROUTING, id).build();

            try {
                JestResult jestResult = this.jestClient.execute(get);
                if (jestResult != null) {

                    Map<String, Object> map = jestResult.getSourceAsObject(Map.class);


                   // UserRealtimeEventActions realtimeEventActions = jestResult.getSourceAsObject(UserRealtimeEventActions.class);
                    if (map != null) {

                        List<String> skus = (List<String>) map.get("skus");

                        if (skus != null && skus.size() > 0) {

                            Set<UserActionData> list = new TreeSet<>();

                            skus.forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));

                            data.put(eventName, list);

                        }

                    }

                }

            } catch (Exception e) {
                logger.error("用户实时数据 query es error ,params: {}", get.getURI(), e);
            }


        });

        userActionResponseDto.setSize(userActionParameterDto.getSize());
        userActionResponseDto.setData(data);

        return userActionResponseDto;
    }

    @Override
    public UserActionResponseDto mock(UserActionParameterDto userActionParameterDto) {
        return new UserActionResponseDto();
    }

    @Override
    public List<String> getById(String id, String site) {
        String esIndex = this.appRealtimeEventIndex.replace("&&", site);
        Get get = new Get.Builder(esIndex, id).type("log").setParameter(Parameters.ROUTING, id).build();
        try {
            JestResult jestResult = this.jestClient.execute(get);
            if (jestResult != null) {

                Map<String, Object> map = jestResult.getSourceAsObject(Map.class);

                if (map != null) {

                    List<String> skus = (List<String>) map.get("skus");

                    if (skus != null && skus.size() > 0) {

                        return skus;

                    }

                }

            }

        } catch (Exception e) {
            logger.error("用户实时数据 query es error ,params: {}", get.getURI(), e);
        }
        return null;
    }

}
