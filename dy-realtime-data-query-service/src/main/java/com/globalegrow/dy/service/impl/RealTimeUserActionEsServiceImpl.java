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
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
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

    @Value("${app.es.index-type:log}")
    private String indexType;

    @Value("${app.es.scroll:1s}")
    private String scroll;

    /**
     * 从 es 查询用户行为数据
     *
     * @param userActionParameterDto
     * @return
     */
    @Override
    public UserActionResponseDto userActionData(UserActionParameterDto userActionParameterDto) throws IOException, ParseException {
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        List<UserActionDto> list = new ArrayList<>();

        long start = System.currentTimeMillis();

        logger.debug("传入参数:{}", userActionParameterDto);

        JestResult result;
        if (StringUtils.isNotEmpty(userActionParameterDto.getScrollId())) {
            SearchScroll scroll = new SearchScroll.Builder(userActionParameterDto.getScrollId(), this.scroll).build();
            result = jestClient.execute(scroll);
        } else {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // String currentDate = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(System.currentTimeMillis());
            // 查询单个用户行为数据
            if (StringUtils.isNotEmpty(userActionParameterDto.getCookieId())) {
                QueryBuilder qb = QueryBuilders.termsQuery("device_id.keyword", userActionParameterDto.getCookieId());
                queryBuilder.must(qb);
            }
            if (StringUtils.isNotEmpty(userActionParameterDto.getUserId())) {
                QueryBuilder qb = QueryBuilders.termsQuery("user_id.keyword", userActionParameterDto.getUserId());
                queryBuilder.must(qb);
            }
            if (StringUtils.isNotEmpty(userActionParameterDto.getStartDate()) && StringUtils.isNotEmpty(userActionParameterDto.getEndDate())) {
                // 时间限制
                QueryBuilder qbTime = QueryBuilders.rangeQuery("timestamp").from(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.parse(userActionParameterDto.getStartDate()).getTime())
                        .to(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.parse(userActionParameterDto.getEndDate() + "T23:59:59").getTime());
                queryBuilder.must(qbTime);
            }

            // 站点条件过滤
            if (userActionParameterDto.getSite() != null && userActionParameterDto.getSite().size() > 0) {
                QueryBuilder qb = QueryBuilders.termsQuery("site.keyword", userActionParameterDto.getSite());
                queryBuilder.must(qb);
            }
            // 终端条件过滤
            if (userActionParameterDto.getDivice() != null && userActionParameterDto.getDivice().size() > 0) {
                QueryBuilder qb = QueryBuilders.termsQuery("platform.keyword", userActionParameterDto.getDivice());
                queryBuilder.must(qb);
            }
            // 事件类型过滤
            if (StringUtils.isNotEmpty(userActionParameterDto.getType())) {

                String eventName = AppEventEnums.getLogEventNameByLocalName(userActionParameterDto.getType());
                if (StringUtils.isNotEmpty(eventName)) {
                    QueryBuilder qb = QueryBuilders.termsQuery("event_name.keyword", userActionParameterDto.getType());
                    queryBuilder.must(qb);
                }

            }
            SortBuilder sortBuilder = new FieldSortBuilder("timestamp");
            sortBuilder.order(SortOrder.DESC);
            searchSourceBuilder.postFilter(queryBuilder);
            this.logger.debug("elasticsearch 搜索条件: {}", searchSourceBuilder.toString());
            Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
            /*if (userActionParameterDto.getStartDate().equals(userActionParameterDto.getEndDate())) {
                // 只查询一天数据
                builder
                        .addIndex(this.indexPrefix + "-" + userActionParameterDto.getStartDate());
            } else {*/
                builder.addIndex(this.indexAliases);
           // }
            searchSourceBuilder.postFilter(queryBuilder);
            Search search = builder
                    .addType(indexType)
                    .setParameter(Parameters.SIZE, userActionParameterDto.getSize())// 每次传多少条数据
                    .setParameter(Parameters.SCROLL, this.scroll)// 开启游标5分钟
                    .build();
            result = jestClient.execute(search);
            logger.info("query from es cost: {}", System.currentTimeMillis() - start);
            if (result != null) {
                logger.debug("es data: {}", result.getSourceAsString());
                String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
                userActionResponseDto.setScrollId(scrollId);
                //List<UserActionEsDto> esDtos = result.getSourceAsObjectList(UserActionEsDto.class);
                // .collect(Collectors.groupingBy(p -> p.age, Collectors.mapping((Person p) -> p, toList())));
                //Map<String, List<UserActionEsDto>> groupDto =
                result.getSourceAsObjectList(UserActionEsDto.class).stream().collect(Collectors.groupingBy(UserActionEsDto::getDevice_id))
                        .entrySet().stream().forEach(a -> {
                    RealTimeUserActionRedisServiceImpl.handleUserActionData(list, a, logger);
                });
            }

        }


        userActionResponseDto.setSize(userActionParameterDto.getSize());
        userActionResponseDto.setData(list);

        return userActionResponseDto;
    }

    @Override
    public UserActionResponseDto mock(UserActionParameterDto userActionParameterDto) {
        return new UserActionResponseDto();
    }
}
