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
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserBaseInfoServiceEsImpl implements UserBaseInfoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
                logger.error("用户基本信息查询 es 出错", e);
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
            if (StringUtils.isNotEmpty(request.getCookieId())) {
                QueryBuilder device_id = QueryBuilders.termsQuery("device_id.keyword", request.getCookieId());
                queryBuilder.filter(device_id);
            }

            SortBuilder sortBuilder = new FieldSortBuilder("_doc");
            //sortBuilder.order(SortOrder.DESC);

            searchSourceBuilder.query(queryBuilder);
            searchSourceBuilder.sort(sortBuilder);
            Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());

            this.logger.debug("elasticsearch 搜索条件: {}", searchSourceBuilder.toString());

            builder.addIndex(esIndex);
            Search search = builder
                    .addType("log").setParameter(Parameters.SCROLL, this.scrollTime)
                    .build();
            try {
                handleUserBaseInfoResponse(response, jestClient.execute(search));
            } catch (IOException e) {
                logger.error("用户基本信息query es error ,params: {}", searchSourceBuilder.toString(), e);
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
            this.logger.debug("用户基本信息：scroll_id {}", scrollId);
            response.setRequestId(scrollId);
        }
        response.setTotalCount(jsonObject.get("hits").getAsJsonObject().get("total").getAsLong());
    }

    public String getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(String scrollTime) {
        this.scrollTime = scrollTime;
    }
}
