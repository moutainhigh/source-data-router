package com.globalegrow.dy.goods.info.service.impl;

import com.globalegrow.dy.dto.CommonListMapESPageResponse;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.goods.info.dto.GoodsStatisticsRequest;
import com.globalegrow.dy.goods.info.service.GoodsStatisticsInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
@Slf4j
public class GoodsStatisticsInfoServiceESImpl implements GoodsStatisticsInfoService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    // 索引名，site dimension 根据入参进行替换
    @Value("${app.es.goods-statistics-index:dy_site_goods_statistics_dimension}")
    private String goodsStatisticsIndex;
    // 索引名 site dimension 根据入参进行替换
    @Value("${app.es.goods-statistics-index:dy_site_country_goods_statistics_dimension}")
    private String countryGoodsStatisticsIndex;

    @Override
    public CommonListMapESPageResponse goodsStatisticsInfo(GoodsStatisticsRequest request) {
        return null;
    }
}
