package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.service.UserActionQueryAllService;
import com.globalegrow.dy.service.UserBaseInfoService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
@Data
@Service
public class UserActionQueryAllServiceEsImpl implements UserActionQueryAllService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    //@Qualifier("myJestClient")
    private JestClient jestClient;

    @Autowired
    private UserBaseInfoService userBaseInfoService;

    String appIndexPrefix = "dy_app_&&_event";

    private String scrollTime = "1m";

    private Long oneDay = 86400000L;

    private Integer maxEarliestDays = 90;

    private Integer defaultEarliestDsys = 30;

    @Override
    public UserActionQueryAllResponse getAllUserActions(UserActionQueryAllRequest request) {

        Long currentTime = System.currentTimeMillis();
        Long startDate = request.getStartDate();

        if (startDate < (currentTime - this.oneDay * this.maxEarliestDays)) {
            this.logger.info("输入时间超过{}天，设置为 {} 天", this.maxEarliestDays, this.defaultEarliestDsys);
            startDate = this.oneDay * this.defaultEarliestDsys;
        }

        UserActionQueryAllResponse allResponse = new UserActionQueryAllResponse();

        UserBaseInfoRequest userBaseInfoRequest = new UserBaseInfoRequest();
        BeanUtils.copyProperties(request, userBaseInfoRequest);

        // 查询用户基本信息
        UserBaseInfoResponse userBaseInfoResponse = this.userBaseInfoService.getUsersBaseInfo(userBaseInfoRequest);
        allResponse.setTotalCount(userBaseInfoResponse.getTotalCount());
        List<UserBaseInfo> userBaseInfos = userBaseInfoResponse.getData();

        if (userBaseInfos != null && userBaseInfos.size() > 0) {
            allResponse.setRequestId(userBaseInfoResponse.getRequestId());

            Map<String, Map<String, Set<UserActionData>>> userActions = new HashMap<>();

            for (UserBaseInfo userBaseInfo : userBaseInfos) {

                Map<String, Set<UserActionData>> actions = new HashMap<>();

                userActions.put(userBaseInfo.getDevice_id(), actions);

                for (String eventName : request.getType()) {
                    List<UserActionData> userActionData = this.getAllUserActionsByDeviceId(userBaseInfo.getDevice_id(), eventName, request.getSite().toLowerCase(), startDate);

                    actions.put(eventName, new TreeSet<>(userActionData));

                }


            }

            allResponse.setData(userActions);

        }


        return allResponse;
    }

    /**
     * 查询指定时间点之后的所有用户事件数据
     *
     * @param deviceId
     * @param eventName
     * @param site
     * @param startDate
     * @return
     */
    private List<UserActionData> getAllUserActionsByDeviceId(String deviceId, String eventName, String site, Long startDate) {
        List<UserActionData> userActionData = new ArrayList<>();
        String esIndex = appIndexPrefix.replace("&&", site);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(1000);

        QueryBuilder timeFilter = QueryBuilders.rangeQuery("timestamp");
        ((RangeQueryBuilder) timeFilter).gte(startDate);
        queryBuilder.filter(timeFilter);

        QueryBuilder device_id = QueryBuilders.termsQuery("device_id.keyword", deviceId);
        queryBuilder.filter(device_id);

        SortBuilder sortBuilder = new FieldSortBuilder("_doc");
        //sortBuilder.order(SortOrder.DESC);


        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.sort(sortBuilder);
        Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());

        this.logger.debug("elasticsearch 搜索条件: {}", searchSourceBuilder.toString());

        builder.addIndex(esIndex + "-" + eventName);
        Search search = builder
                .addType("log").setParameter(Parameters.ROUTING, deviceId).setParameter(Parameters.SCROLL, this.scrollTime)
                .build();

        try {
            JestResult result = this.jestClient.execute(search);
            JsonObject jsonObject = result.getJsonObject();
            JsonElement jsonElement = jsonObject.get("_scroll_id");


            Long total = jsonObject.get("hits").getAsJsonObject().get("total").getAsLong();
            //int hints = jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray().size();
            if (total > 0) {

                result.getSourceAsObjectList(UserActionEsDto.class).stream().collect(Collectors.groupingBy(UserActionEsDto::getEvent_name)).entrySet().stream().forEach(e ->
                        userActionData.addAll(e.getValue().stream().map(esd -> new UserActionData(esd.getEvent_value(), esd.getTimestamp())).collect(Collectors.toList())));

                if (jsonElement != null) {
                    String scrollId = jsonElement.getAsString();
                    this.logger.debug("用户基本信息：scroll_id {}", scrollId);
                    this.handleUserActionScroll(userActionData, scrollId);
                }

            }

        } catch (IOException e) {
            logger.error("查询用户全部事件信息query es error ,params: {}", searchSourceBuilder.toString(), e);
        }


        return userActionData;

    }

    public void handleUserActionScroll(List<UserActionData> userActionData, String scrollId) throws IOException {
        SearchScroll scroll = new SearchScroll.Builder(scrollId, this.scrollTime).build();
        JestResult result = this.jestClient.execute(scroll);
        JsonObject jsonObject = result.getJsonObject();
        JsonElement jsonElement = jsonObject.get("_scroll_id");
        if (jsonElement != null) {

            String scrollId_n = jsonElement.getAsString();
            int hints = jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray().size();
            if (hints > 0) {
                result.getSourceAsObjectList(UserActionEsDto.class).stream().collect(Collectors.groupingBy(UserActionEsDto::getEvent_name)).entrySet().stream().forEach(e ->
                        userActionData.addAll(e.getValue().stream().map(esd -> new UserActionData(esd.getEvent_value(), esd.getTimestamp())).collect(Collectors.toList())));
                handleUserActionScroll(userActionData, scrollId_n);
            }

        }


    }

}
