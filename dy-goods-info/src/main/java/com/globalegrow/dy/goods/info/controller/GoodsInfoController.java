package com.globalegrow.dy.goods.info.controller;

import com.globalegrow.dy.dto.CommonListMapESPageResponse;
import com.globalegrow.dy.goods.info.dto.GoodsStatisticsRequest;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("goods")
public class GoodsInfoController {

    @RequestMapping("statistics")
    public CommonListMapESPageResponse goodsStatisticsInfo(GoodsStatisticsRequest request){


        return null;
    }

}
