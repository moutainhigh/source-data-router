package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.UserBaseInfo;
import com.globalegrow.dy.dto.UserBaseInfoRequest;
import com.globalegrow.dy.dto.UserBaseInfoResponse;
import com.globalegrow.dy.service.UserBaseInfoService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Data
@Slf4j
@Service
public class UserBaseInfoServiceEsImpl implements UserBaseInfoService {

    @Autowired
    //@Qualifier("myJestClient")
    private JestClient jestClient;

    String appIndexPrefix = "dy_app_&&_user_base";

    private String scrollTime = "1m";


    @Override
    public UserBaseInfoResponse getUsersBaseInfo(UserBaseInfoRequest request) {
        UserBaseInfoResponse response = new UserBaseInfoResponse();

        if (StringUtils.isNotEmpty(request.getRequestId())) {
            SearchScroll scroll = new SearchScroll.Builder(request.getRequestId(), this.scrollTime).build();
            try {
                handleUserBaseInfoResponse(response, jestClient.execute(scroll));
            } catch (Exception e) {
                log.error("用户基本信息查询 es 出错", e);
                response.setSuccess(false);
            }

        } else {
            String esIndex = appIndexPrefix.replace("&&", request.getSite().toLowerCase());
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(request.getSize());
            // 终端过滤
            if (request.getPlatform() != null && request.getPlatform().size() > 0) {
                QueryBuilder platform = QueryBuilders.termsQuery("platform.keyword", request.getPlatform());
                queryBuilder.filter(platform);
            }
            // 按用户设备 id 过滤
            if (StringUtils.isNotBlank(request.getCookieId())) {
                QueryBuilder device_id = QueryBuilders.termsQuery("device_id.keyword", request.getCookieId());
                queryBuilder.filter(device_id);
            }
            // 按用户 id 过滤
            if (StringUtils.isNotBlank(request.getUserId())) {
                QueryBuilder user_id = QueryBuilders.termsQuery("user_id.keyword", request.getUserId());
                queryBuilder.filter(user_id);
            }
            // 按时间过滤
            if (request.getStartDate() != null) {
                QueryBuilder timeFilter = QueryBuilders.rangeQuery("timestamp");
                ((RangeQueryBuilder) timeFilter).gte(request.getStartDate());
                queryBuilder.filter(timeFilter);
            }

            SortBuilder sortBuilder = new FieldSortBuilder("_doc");
            //sortBuilder.order(SortOrder.DESC);

            searchSourceBuilder.query(queryBuilder);

            searchSourceBuilder.sort(sortBuilder);
            Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());

            log.debug("elasticsearch 搜索条件: {}", searchSourceBuilder.toString());

            builder.addIndex(esIndex);
            Search search = builder
                    .addType("log").setParameter(Parameters.SCROLL, this.scrollTime)
                    .build();
            try {
                handleUserBaseInfoResponse(response, jestClient.execute(search));
            } catch (IOException e) {
                log.error("用户基本信息query es error ,params: {}", searchSourceBuilder.toString(), e);
            }

        }

        return response;
    }

    private void handleUserBaseInfoResponse(UserBaseInfoResponse response, JestResult result) throws IOException {
        response.setData(result.getSourceAsObjectList(UserBaseInfo.class));
        JsonObject jsonObject =  result.getJsonObject();
        JsonElement jsonElement = jsonObject.get("_scroll_id");
        if (jsonElement != null) {
            String scrollId = jsonElement.getAsString();
            log.debug("用户基本信息：scroll_id {}", scrollId);
            response.setRequestId(scrollId);
        }
        response.setTotalCount(jsonObject.get("hits").getAsJsonObject().get("total").getAsLong());
    }

}
