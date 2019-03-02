package com.globalegrow.dy.goods.info.service;

import com.globalegrow.dy.dto.CommonListMapResponse;
import com.globalegrow.dy.goods.info.dto.GoodsBaseInfoRequest;

public interface GoodsBaseInfoService {

    /**
     * 商品基本信息
     * @param request
     * @return
     */
    CommonListMapResponse goodsBaseInfo(GoodsBaseInfoRequest request);

}
