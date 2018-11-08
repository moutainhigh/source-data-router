package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.google.gson.JsonObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
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

    @PostConstruct
    public void before(){

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
        Map<String, List<UserActionData>> data = new HashMap<>();
        List<String> inputType = userActionParameterDto.getType();
        if (inputType == null){
            inputType = new ArrayList<>();
        }
        if (inputType.size() < 1) {
            inputType.addAll(Arrays.stream(AppEventEnums.values()).map(AppEventEnums :: name).collect(Collectors.toList()));
        }
        inputType.parallelStream().forEach(eventName -> {
            long start = System.currentTimeMillis();

            logger.debug("传入参数:{}", userActionParameterDto);

            JestResult result = null;

            this.logger.debug("第二次查询");

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // 查询单个用户行为数据
            QueryBuilder qb = QueryBuilders.termQuery("device_id", userActionParameterDto.getCookieId());
            queryBuilder.filter(qb);

            // 时间限制
            if (userActionParameterDto.getStartDate() != null && userActionParameterDto.getEndDate() != null) {
                QueryBuilder qbTime = QueryBuilders.rangeQuery("timestamp").gte(userActionParameterDto.getStartDate())
                        .lte(userActionParameterDto.getEndDate()).includeLower(false).includeUpper(false).boost(2.0F);
                queryBuilder.filter(qbTime);
            }

            // 事件类型过滤
            QueryBuilder qbevent = QueryBuilders.termQuery("event_name", eventName);
            queryBuilder.filter(qbevent);

            // 站点条件过滤
            if (userActionParameterDto.getSite() != null && userActionParameterDto.getSite().size() > 0) {
                QueryBuilder qbs = QueryBuilders.termsQuery("site", userActionParameterDto.getSite());
                queryBuilder.filter(qbs);
            }
            // 终端条件过滤
            if (userActionParameterDto.getPlatform() != null && userActionParameterDto.getPlatform().size() > 0) {
                QueryBuilder qbd = QueryBuilders.termsQuery("platform", userActionParameterDto.getPlatform());
                queryBuilder.filter(qbd);
            }

            SortBuilder sortBuilder = new FieldSortBuilder("timestamp");
            sortBuilder.order(SortOrder.DESC);

            searchSourceBuilder.from(0);
            searchSourceBuilder.size(userActionParameterDto.getSize());

            searchSourceBuilder.query(queryBuilder);
            searchSourceBuilder.sort(sortBuilder);
            Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
            this.logger.debug("elasticsearch 搜索条件: {}", searchSourceBuilder.toString());
            builder.addIndex(this.indexAliases);
            Search search = builder
                    .addType(indexType).setParameter(Parameters.ROUTING, userActionParameterDto.getCookieId())
                    .build();

            try {
                result = jestClient.execute(search);
            } catch (IOException e) {
                logger.error("query es error ,params: {}", searchSourceBuilder.toString(), e);
            }
            logger.info("query from es cost: {}", System.currentTimeMillis() - start);


            if (result != null) {
                long handleStart = System.currentTimeMillis();
                logger.debug("es data result size: {}", result.getSourceAsObjectList(UserActionEsDto.class).size());
                result.getSourceAsObjectList(UserActionEsDto.class).parallelStream().collect(Collectors.groupingBy(UserActionEsDto::getEvent_name)).entrySet().stream().forEach(e -> {
                    data.put(e.getKey(), e.getValue().parallelStream().map(esd -> new UserActionData(esd.getEvent_value(), esd.getTimestamp())).collect(Collectors.toList()));
                });
                logger.debug("handle result costs: {}", System.currentTimeMillis() - handleStart);
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
}
