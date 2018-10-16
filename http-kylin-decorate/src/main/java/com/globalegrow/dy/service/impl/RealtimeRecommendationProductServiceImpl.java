package com.globalegrow.dy.service.impl;

import cn.hutool.core.util.StrUtil;
import com.globalegrow.dy.dto.SkuRequest;
import com.globalegrow.dy.dto.SkuResponse;
import com.globalegrow.dy.service.RealtimeRecommendationProductService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
import jdk.nashorn.internal.runtime.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 实时推荐数据分析-商品维度
 * @Author chongzi
 * @Date 2018/10/10 9:42
 **/
@Service
public class RealtimeRecommendationProductServiceImpl implements RealtimeRecommendationProductService {
    public final static String INIT_START_DATE = "1900-01-01";
    public final static String INIT_END_DATE = "2099-12-30";

    @Autowired
    private JestClient jestClient;

    @Override
    public Map<String, Object> getSkuInfo(SkuRequest skuRequest) throws IOException {
        Map<String, Object> mapResult = new LinkedHashMap<>();
        JestResult result;
        int size = 10;
        if(StrUtil.isNotBlank(skuRequest.getScrollId())){
            SearchScroll scroll = new SearchScroll.Builder(skuRequest.getScrollId(),"5m").build();
            result = jestClient.execute(scroll);
        }else{
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if(StrUtil.isNotBlank(skuRequest.getSku())){
                QueryBuilder qb = QueryBuilders.termsQuery("sku", skuRequest.getSku());
                queryBuilder.must(qb);
            }
            if(StrUtil.isNotBlank(skuRequest.getStartDate())|| StrUtil.isNotBlank(skuRequest.getEndDate())){
                String startDate = StringUtils.isNotBlank(skuRequest.getStartDate()) ? skuRequest.getStartDate() : INIT_START_DATE;
                String endDate = StringUtils.isNotBlank(skuRequest.getEndDate()) ? skuRequest.getStartDate() : INIT_END_DATE;
                QueryBuilder qb = QueryBuilders.rangeQuery("eventDate").from(startDate).to(endDate);
                queryBuilder.must(qb);
            }
            if(StrUtil.isNotBlank(skuRequest.getSite())){
                QueryBuilder qb = QueryBuilders.termsQuery("site", skuRequest.getSku());
                queryBuilder.must(qb);
            }
            if(StrUtil.isNotBlank(skuRequest.getDivice())){
                QueryBuilder qb = QueryBuilders.termsQuery("divice", skuRequest.getSku());
                queryBuilder.must(qb);
            }
            if(skuRequest.getSize() != null && skuRequest.getSize() > 0){
                size = skuRequest.getSize();
            }
            searchSourceBuilder.postFilter(queryBuilder);
            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex("recommendation-statistics")
                    .addType("productDateStatistics")
                    .setParameter(Parameters.SIZE,size)// 每次传多少条数据
                    .setParameter(Parameters.SCROLL,"5m")// 开启游标5分钟
                    .build();
             result = jestClient.execute(search);
        }
        JsonElement jsonElement = result.getJsonObject().getAsJsonObject("hits").get("total");
        int total = 0;
        if(jsonElement != null){
            total = Integer.valueOf(jsonElement.toString());
        }
        List<SkuResponse> skuResponseList = result.getSourceAsObjectList(SkuResponse.class);
        String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
        mapResult.put("success", true);
        mapResult.put("message", null);
        mapResult.put("total", total);
        mapResult.put("scrollId", scrollId);
        mapResult.put("date", skuResponseList);
        return mapResult;
    }
}
