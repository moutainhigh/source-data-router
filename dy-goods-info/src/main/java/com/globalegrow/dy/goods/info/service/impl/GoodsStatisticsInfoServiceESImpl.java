package com.globalegrow.dy.goods.info.service.impl;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.dy.dto.CommonListMapESPageResponse;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.goods.info.dto.GoodsStatisticsRequest;
import com.globalegrow.dy.goods.info.service.GoodsStatisticsInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
@Service
@Slf4j
public class GoodsStatisticsInfoServiceESImpl implements GoodsStatisticsInfoService {

    @Autowired
    private TransportClient client;

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    // 索引名，site dimension 根据入参进行替换
    @Value("${app.es.goods-statistics-index:dy_site_goods_statistics_$dimension}")
    private String goodsStatisticsIndex;
    // 索引名 site dimension 根据入参进行替换，国家统计维度
    @Value("${app.es.goods-statistics-index:dy_site_country_goods_statistics_$dimension}")
    private String countryGoodsStatisticsIndex;

    @Value("${app.es.goods-statistics-index-type:goods_info}")
    private String indexType;

    /**
     * 分页请求
     *
     * @param request
     * @return
     */
    @Override
    public CommonListMapESPageResponse goodsStatisticsInfo(GoodsStatisticsRequest request) {
        CommonListMapESPageResponse response = new CommonListMapESPageResponse();

        List<Map<String, Object>> mapList = new ArrayList<>();

        if (StringUtils.isNotEmpty(request.getRequestId())) {

            this.esSearch(response, mapList, this.client.prepareSearchScroll(request.getRequestId()).setScroll(new TimeValue(60000)).execute().actionGet());

        }else {
            String indexName = this.goodsStatisticsIndex;
            if (StringUtils.isNotEmpty(request.getCountry())) {
                indexName = this.countryGoodsStatisticsIndex;
            }

            if (request.getDimension() == 1) {

                indexName = indexName.replace("site", request.getSite().toLowerCase()).replace("$dimension", request.getDimension() + "");

            } else {
                //greater_than_1
                indexName = indexName.replace("site", request.getSite().toLowerCase()).replace("$dimension", "greater_than_1");
            }



            // 分页查询
            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(indexName)
                    .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                    .setScroll(new TimeValue(60000)).setQuery(QueryBuilders.termQuery("platform", request.getPlatform()))
                    //.setQuery(qb)
                    .setSize(request.getSize());

            if (StringUtils.isNotEmpty(request.getCountry())) {
                searchRequestBuilder.setQuery(QueryBuilders.termQuery("country", request.getCountry())).get();
            }

            if (request.getDimension() == 1) {

                searchRequestBuilder
                        .setQuery(QueryBuilders.termsQuery("update_day", request.getDays()));

            } else {

                searchRequestBuilder.setQuery(QueryBuilders.termQuery("dimension", request.getDimension()))/*.setQuery(QueryBuilders.termQuery("update_day", DateUtil.yesterday().toString("yyyy-MM-dd")))*/;

            }


            SearchResponse scrollResp = searchRequestBuilder.get();

            this.esSearch(response, mapList, scrollResp);

        }

        response.setData(mapList);
        return response;
    }

    private void esSearch(CommonListMapESPageResponse response, List<Map<String, Object>> mapList, SearchResponse scrollResp) {
        Arrays.stream(scrollResp.getHits().getHits()).forEach(searchHitFields -> mapList.add(searchHitFields.getSourceAsMap()));

        response.setRequestId(scrollResp.getScrollId());
        response.setTotal(scrollResp.getHits().getTotalHits());
    }
}
