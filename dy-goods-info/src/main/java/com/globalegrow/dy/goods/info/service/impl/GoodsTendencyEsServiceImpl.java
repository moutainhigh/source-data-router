package com.globalegrow.dy.goods.info.service.impl;

import com.globalegrow.dy.dto.CommonListMapResponse;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.goods.info.dto.GoodsTendencyRequest;
import com.globalegrow.dy.goods.info.service.GoodsTendencyService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
@Slf4j
@Service
public class GoodsTendencyEsServiceImpl implements GoodsTendencyService {

    @Autowired
    private TransportClient client;

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Value("${app.es.goods-tendency-index:dy_site_goods_statistics_tendency}")
    private String goodsTendencyIndex;

    @Value("${app.es.goods-country-tendency-index:dy_site_goods_statistics_tendency_country}")
    private String goodsCountryTendencyIndex;

    @Value("${app.es.goods-info-index-type:goods_info}")
    private String goodsIndexType;


    @Override
    public CommonListMapResponse goodsTendency(GoodsTendencyRequest request) {
        CommonListMapResponse response = new CommonListMapResponse();



        String indexName = this.goodsTendencyIndex.replace("site", request.getSite());

        if (StringUtils.isNotEmpty(request.getCountry())) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()/*.must(QueryBuilders.termQuery("platform.keyword", request.getPlatform()))*/;
            indexName = this.goodsCountryTendencyIndex.replace("site", request.getSite());
            boolQueryBuilder.must(QueryBuilders.termQuery("country.keyword", request.getCountry())).must(QueryBuilders.termsQuery("_id", request.getIds()));

            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(indexName)
                    .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                    .setQuery(boolQueryBuilder)
                    .setSize(request.getSize());

            log.debug("搜索条件 {}", searchRequestBuilder.toString());

            List<Map<String, Object>> mapList = new ArrayList<>();

            SearchResponse scrollResp = searchRequestBuilder.get();

            Arrays.stream(scrollResp.getHits().getHits()).forEach(searchHitFields -> mapList.add(searchHitFields.getSourceAsMap()));

            response.setData(mapList);

            return response;
        }else {
            response.setData(this.elasticSearchRepository.idInSearch(indexName, this.goodsIndexType, request.getIds()));

            return response;
        }

    }


}
