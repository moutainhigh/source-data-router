package com.globalegrow.dy.goods.info.controller;

import com.globalegrow.dy.dto.CommonListMapESPageResponse;
import com.globalegrow.dy.goods.info.dto.GoodsStatisticsRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("goods")
public class GoodsInfoController {

    @PostMapping(value = "statistics", produces = "application/json;charset=UTF-8")
    public CommonListMapESPageResponse goodsStatisticsInfo(@Validated @RequestBody GoodsStatisticsRequest request){
        if (request.getDimension() == 1 && (request.getDays() == null || request.getDays().size() == 0)) {
            CommonListMapESPageResponse response = new CommonListMapESPageResponse();
            response.setSuccess(false);
            response.setMessage("统计维度为 1 时，查询天为必填");
            return response;
        }

        return null;
    }

}
