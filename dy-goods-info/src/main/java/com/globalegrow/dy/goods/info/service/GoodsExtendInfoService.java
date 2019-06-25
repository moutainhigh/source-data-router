package com.globalegrow.dy.goods.info.service;

import com.globalegrow.dy.dto.CommonMapResponse;
import com.globalegrow.dy.goods.info.dto.GoodsExtendInfoRequest;

public interface GoodsExtendInfoService {

    CommonMapResponse getGoodsExtendInfo(GoodsExtendInfoRequest request);

}
