package com.globalegrow.dy.goods.info.service.impl;

import com.globalegrow.dy.dto.CommonListMapResponse;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.goods.info.dto.GoodsBaseInfoRequest;
import com.globalegrow.dy.goods.info.service.GoodsBaseInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
@Slf4j
public class GoodsBaseInfoServiceESImpl implements GoodsBaseInfoService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Value("${app.es.goods-info-index:dy_site_goods_info}")
    private String goodsInfoIndex;

    /**
     * 商品基本信息，从 es 查询，sku 为 索引主键，采用 id in 查询
     *
     * @param request
     * @return
     */
    @Override
    public CommonListMapResponse goodsBaseInfo(GoodsBaseInfoRequest request) {
        return null;
    }
}
