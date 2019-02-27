package com.globalegrow.dy.goods.info.service;

import com.globalegrow.dy.dto.CommonListMapESPageResponse;
import com.globalegrow.dy.goods.info.dto.GoodsStatisticsRequest;

public interface GoodsStatisticsInfoService {

    CommonListMapESPageResponse goodsStatisticsInfo(GoodsStatisticsRequest request);

}
