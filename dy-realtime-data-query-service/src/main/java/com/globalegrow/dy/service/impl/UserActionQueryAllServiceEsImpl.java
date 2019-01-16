package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.service.UserActionQueryAllService;
import com.globalegrow.dy.service.UserBaseInfoService;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserActionQueryAllServiceEsImpl implements UserActionQueryAllService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("myJestClient")
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

        if (startDate < (currentTime - this.oneDay*this.maxEarliestDays)) {
            this.logger.info("输入时间超过{}天，设置为 {} 天", this.maxEarliestDays, this.defaultEarliestDsys);
            startDate = currentTime = this.oneDay*this.defaultEarliestDsys;
        }

        UserActionQueryAllResponse allResponse = new UserActionQueryAllResponse();

        UserBaseInfoRequest userBaseInfoRequest = new UserBaseInfoRequest();
        BeanUtils.copyProperties(request, userBaseInfoRequest);

        // 查询用户基本信息
        UserBaseInfoResponse userBaseInfoResponse = this.userBaseInfoService.getUsersBaseInfo(userBaseInfoRequest);
        List<UserBaseInfo> userBaseInfos = userBaseInfoResponse.getData();

        if (userBaseInfos != null && userBaseInfos.size() > 0) {
            allResponse.setRequestId(userBaseInfoResponse.getRequestId());

            Map<String, Map<String, List<UserActionData>>> userActions = new HashMap<>();

            for (UserBaseInfo userBaseInfo : userBaseInfos) {

                Map<String, List<UserActionData>> actions = new HashMap<>();

                userActions.put(userBaseInfo.getDevice_id(), actions);

                for (String eventName : request.getType()) {

                    actions.put(eventName, this.getAllUserActionsByDeviceId(userBaseInfo.getDevice_id(), eventName, request.getSite(), startDate));

                }


            }

            allResponse.setData(userActions);

        }


        return allResponse;
    }

    /**
     * 查询指定时间点之后的所有用户事件数据
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
        builder.addIndex(esIndex + "-" + eventName);
        Search search = builder
                .addType("log").setParameter(Parameters.ROUTING, device_id).setParameter(Parameters.SCROLL, this.scrollTime)
                .build();

        try {
            JestResult result = this.jestClient.execute(search);
            JsonObject jsonObject =  result.getJsonObject();
            String scrollId = jsonObject.get("_scroll_id").getAsString();
            Long total = jsonObject.get("hits").getAsJsonObject().get("total").getAsLong();
            //int hints = jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray().size();
            if (total > 0) {
                result.getSourceAsObjectList(UserActionEsDto.class).stream().collect(Collectors.groupingBy(UserActionEsDto::getEvent_name)).entrySet().stream().forEach(e ->
                        userActionData.addAll(e.getValue().stream().map(esd -> new UserActionData(esd.getEvent_value(), esd.getTimestamp())).collect(Collectors.toList())));
                this.handleUserActionScroll(userActionData, scrollId);
            }

        } catch (IOException e) {
            logger.error("查询用户全部事件信息query es error ,params: {}", searchSourceBuilder.toString(), e);
        }


        return userActionData;

    }

    public void handleUserActionScroll(List<UserActionData> userActionData, String scrollId) throws IOException {
        SearchScroll scroll = new SearchScroll.Builder(scrollId, this.scrollTime).build();
        JestResult result = this.jestClient.execute(scroll);
        JsonObject jsonObject =  result.getJsonObject();
        String scrollId_n = jsonObject.get("_scroll_id").getAsString();
        int hints = jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray().size();
        if (hints > 0) {
            result.getSourceAsObjectList(UserActionEsDto.class).stream().collect(Collectors.groupingBy(UserActionEsDto::getEvent_name)).entrySet().stream().forEach(e ->
                    userActionData.addAll(e.getValue().stream().map(esd -> new UserActionData(esd.getEvent_value(), esd.getTimestamp())).collect(Collectors.toList())));
            handleUserActionScroll(userActionData, scrollId_n);
        }

    }

    public Integer getMaxEarliestDays() {
        return maxEarliestDays;
    }

    public void setMaxEarliestDays(Integer maxEarliestDays) {
        this.maxEarliestDays = maxEarliestDays;
    }

    public Integer getDefaultEarliestDsys() {
        return defaultEarliestDsys;
    }

    public void setDefaultEarliestDsys(Integer defaultEarliestDsys) {
        this.defaultEarliestDsys = defaultEarliestDsys;
    }

    public String getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(String scrollTime) {
        this.scrollTime = scrollTime;
    }
}
