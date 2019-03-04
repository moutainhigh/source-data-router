package com.globalegrow.dy.goods.info.service.impl;

import com.globalegrow.dy.costants.DyConstants;
import com.globalegrow.dy.dto.CommonListMapResponse;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.goods.info.dto.GoodsBaseInfoRequest;
import com.globalegrow.dy.goods.info.service.GoodsBaseInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
@Slf4j
public class GoodsBaseInfoServiceESImpl implements GoodsBaseInfoService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Autowired
    private TransportClient client;

    @Value("${app.es.goods-info-index:dy_site_goods_info}")
    private String goodsInfoIndex;

    @Value("${app.es.goods-info-index-type:goods_info}")
    private String goodsIndexType;

    /**
     * 商品基本信息，从 es 查询，sku 为 索引主键，采用 id in 查询
     *
     * @param request
     * @return
     */
    @Override
    public CommonListMapResponse goodsBaseInfo(GoodsBaseInfoRequest request) {
        CommonListMapResponse response = new CommonListMapResponse();
        response.setData(this.elasticSearchRepository.idInSearch(this.goodsInfoIndex.replace(DyConstants.ES_INDEX_NAME_SITE_SPLIT, request.getSite().toLowerCase()), this.goodsIndexType, request.getItem_ids()));
        return response;
    }
}
