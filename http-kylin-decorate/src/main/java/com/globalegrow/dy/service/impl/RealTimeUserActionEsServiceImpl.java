package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.UserActionDto;
import com.globalegrow.dy.dto.UserActionEsDto;
import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealTimeUserActionEsServiceImpl implements RealTimeUserActionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
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

    /**
     * 从 es 查询用户行为数据
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException {
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        logger.debug("传入参数:{}", userActionParameterDto);
        List<UserActionDto> list = new ArrayList<>();
        JestResult result;
        if(StringUtils.isNotEmpty(userActionParameterDto.getScrollId())){
            SearchScroll scroll = new SearchScroll.Builder(userActionParameterDto.getScrollId(),"5m").build();
            result = jestClient.execute(scroll);
        }else {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            String currentDate = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(System.currentTimeMillis());
            // 查询单个用户行为数据
            if (StringUtils.isNotEmpty(userActionParameterDto.getCookieId())) {
                QueryBuilder qb = QueryBuilders.termsQuery("device_id.keyword", userActionParameterDto.getCookieId());
                queryBuilder.must(qb);
            }
            if (StringUtils.isNotEmpty(userActionParameterDto.getUserId())) {
                QueryBuilder qb = QueryBuilders.termsQuery("user_id", userActionParameterDto.getUserId());
                queryBuilder.must(qb);
            }
            // 时间限制
            QueryBuilder qbTime = QueryBuilders.rangeQuery("timestamp").from(userActionParameterDto.getStartDate()).to(userActionParameterDto.getEndDate());
            queryBuilder.must(qbTime);
            // 站点条件过滤
            if (userActionParameterDto.getSite() != null && userActionParameterDto.getSite().size() > 0) {
                QueryBuilder qb = QueryBuilders.termsQuery("site", userActionParameterDto.getSite());
                queryBuilder.must(qb);
            }
            // 终端条件过滤
            if (userActionParameterDto.getDivice() != null && userActionParameterDto.getDivice().size() > 0) {
                QueryBuilder qb = QueryBuilders.termsQuery("platform", userActionParameterDto.getDivice());
                queryBuilder.must(qb);
            }
            // 事件类型过滤
            if (StringUtils.isNotEmpty(userActionParameterDto.getType())) {

                String eventName = AppEventEnums.getLogEventNameByLocalName(userActionParameterDto.getType());
                if (StringUtils.isNotEmpty(eventName)) {
                    QueryBuilder qb = QueryBuilders.termsQuery("event_name", userActionParameterDto.getType());
                    queryBuilder.must(qb);
                }

            }
            searchSourceBuilder.postFilter(queryBuilder);
            this.logger.debug("elasticsearch 搜索条件: {}", searchSourceBuilder.toString());
            Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
            if (currentDate.equals(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(userActionParameterDto.getStartDate())) &&
                    currentDate.equals(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(userActionParameterDto.getEndDate()))) {
                // 只查询当天的索引数据
                builder
                        .addIndex(this.indexPrefix + currentDate);
            } else {
                builder.addIndex(this.indexAliases);
            }
            searchSourceBuilder.postFilter(queryBuilder);
            Search search = builder
                    .addType("log")
                    .setParameter(Parameters.SIZE, 10)// 每次传多少条数据
                    .setParameter(Parameters.SCROLL,"5m")// 开启游标5分钟
                    .build();
            result = jestClient.execute(search);

        }

        if (result != null) {
            String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
            userActionResponseDto.setScrollId(scrollId);
            //List<UserActionEsDto> esDtos = result.getSourceAsObjectList(UserActionEsDto.class);
            // .collect(Collectors.groupingBy(p -> p.age, Collectors.mapping((Person p) -> p, toList())));
            //Map<String, List<UserActionEsDto>> groupDto =
            result.getSourceAsObjectList(UserActionEsDto.class).parallelStream().collect(Collectors.groupingBy(UserActionEsDto :: getDevice_id))
           .entrySet().parallelStream().forEach(a -> {
                RealTimeUserActionRedisServiceImpl.handleUserActionData(list, a, logger);
            });
        }
        userActionResponseDto.setData(list);
        return userActionResponseDto;
    }
}
