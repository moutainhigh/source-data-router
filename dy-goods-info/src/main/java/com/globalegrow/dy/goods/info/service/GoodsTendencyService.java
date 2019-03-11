package com.globalegrow.dy.goods.info.service;

import com.globalegrow.dy.dto.CommonListMapResponse;
import com.globalegrow.dy.goods.info.dto.GoodsTendencyRequest;

public interface GoodsTendencyService {

    CommonListMapResponse goodsTendency(GoodsTendencyRequest request);

}
