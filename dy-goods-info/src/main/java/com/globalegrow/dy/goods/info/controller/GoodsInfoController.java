package com.globalegrow.dy.goods.info.controller;

import com.globalegrow.dy.controller.CommonController;
import com.globalegrow.dy.dto.CommonListMapESPageResponse;
import com.globalegrow.dy.dto.CommonListMapResponse;
import com.globalegrow.dy.goods.info.dto.GoodsBaseInfoRequest;
import com.globalegrow.dy.goods.info.dto.GoodsStatisticsRequest;
import com.globalegrow.dy.goods.info.dto.GoodsTendencyRequest;
import com.globalegrow.dy.goods.info.service.GoodsBaseInfoService;
import com.globalegrow.dy.goods.info.service.GoodsStatisticsInfoService;
import com.globalegrow.dy.goods.info.service.GoodsTendencyService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@Slf4j
@RestController
@RequestMapping("goods")
public class GoodsInfoController extends CommonController {

    @Autowired
    private GoodsBaseInfoService goodsBaseInfoService;

    @Autowired
    private GoodsStatisticsInfoService goodsStatisticsInfoService;

    @Autowired
    private GoodsTendencyService goodsTendencyService;

    @PostMapping(value = "statistics", produces = "application/json;charset=UTF-8")
    public CommonListMapESPageResponse goodsStatisticsInfo(@Validated @RequestBody GoodsStatisticsRequest request){
        if (request.getDimension() == 1 && (request.getDays() == null || request.getDays().size() == 0)) {
            CommonListMapESPageResponse response = new CommonListMapESPageResponse();
            response.setSuccess(false);
            response.setMessage("统计维度为 1 时，查询天为必填");
            return response;
        }
        if (request.getDimension() == 1 && request.getDays().size() > 30) {
            CommonListMapESPageResponse response = new CommonListMapESPageResponse();
            response.setSuccess(false);
            response.setMessage("统计维度为 1 时，查询天最多为 30 天");
            return response;
        }

        return this.goodsStatisticsInfoService.goodsStatisticsInfo(request);
    }

    @PostMapping(value = "base-info", produces = "application/json;charset=UTF-8")
    public CommonListMapResponse goodsBaseInfo(@Validated @RequestBody GoodsBaseInfoRequest request){
        return this.goodsBaseInfoService.goodsBaseInfo(request);
    }

    @PostMapping(value = "tendency", produces = "application/json;charset=UTF-8")
    public CommonListMapResponse goodsTendency(@Validated @RequestBody GoodsTendencyRequest request){
        return this.goodsTendencyService.goodsTendency(request);
    }

}
