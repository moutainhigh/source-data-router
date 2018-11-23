package com.globalegrow.dy.service.impl;

import cn.hutool.core.util.StrUtil;
import com.globalegrow.dy.dto.SkuRequest;
import com.globalegrow.dy.dto.SkuResponse;
import com.globalegrow.dy.enums.SkuRequestTypeEnum;
import com.globalegrow.dy.service.RealtimeRecommendationProductService;
import com.google.gson.JsonElement;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        String message = null;
        boolean success = true;
        int size = 10000;
        String esIndex = "dy-app-product-date";
        String esType = "statistics";
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

            if(skuRequest.getSite() != null && skuRequest.getSite().size() > 0){
                QueryBuilder qb = QueryBuilders.termsQuery("site", skuRequest.getSku());
                queryBuilder.must(qb);
            }
            if(skuRequest.getDivice() != null && skuRequest.getDivice().size() > 0){
                QueryBuilder qb = QueryBuilders.termsQuery("divice", skuRequest.getSku());
                queryBuilder.must(qb);
            }
            if(skuRequest.getSize() != null && skuRequest.getSize() > 0 && skuRequest.getSize() <= 10000){
                size = skuRequest.getSize();
            }
            if(skuRequest.getType() != null){
                if(skuRequest.getType() == SkuRequestTypeEnum.ZERO.getCode()){
                     esIndex = "dy-app-product-date";
                     esType = "statistics";
                    if(StrUtil.isNotBlank(skuRequest.getStartDate())|| StrUtil.isNotBlank(skuRequest.getEndDate())){
                        String startDate = StringUtils.isNotBlank(skuRequest.getStartDate()) ? skuRequest.getStartDate() : INIT_START_DATE;
                        String endDate = StringUtils.isNotBlank(skuRequest.getEndDate()) ? skuRequest.getStartDate() : INIT_END_DATE;
                        QueryBuilder qb = QueryBuilders.rangeQuery("eventDate").from(startDate).to(endDate);
                        queryBuilder.must(qb);
                    }
                }else if(skuRequest.getType() == SkuRequestTypeEnum.ONE.getCode()){
                    esIndex = "dy-app-product-month";
                    esType = "statistics";
                }else if(skuRequest.getType() == SkuRequestTypeEnum.TWO.getCode()){
                    esIndex = "dy-app-product-month-two";
                    esType = "statistics";
                }else if(skuRequest.getType() == SkuRequestTypeEnum.THREE.getCode()){
                    esIndex = "dy-app-product-month-three";
                    esType = "statistics";
                }else if(skuRequest.getType() == SkuRequestTypeEnum.SIX.getCode()){
                    esIndex = "dy-app-product-month-six";
                    esType = "statistics";
                }else{
                    esIndex = "dy-app-product-date";
                    esType = "statistics";
                }
            }else{
                success = false;
                message = "查询日期类型不能为空";
                mapResult.put("success", success);
                mapResult.put("message", message);
                mapResult.put("total", 0);
                mapResult.put("scrollId", null);
                mapResult.put("date", null);
                return mapResult;
            }
            searchSourceBuilder.postFilter(queryBuilder);
            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex(esIndex)
                    .addType(esType)
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
        mapResult.put("success", success);
        mapResult.put("message", message);
        mapResult.put("total", total);
        mapResult.put("scrollId", scrollId);
        mapResult.put("date", skuResponseList);
        return mapResult;
    }
}
