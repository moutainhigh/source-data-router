package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.SkuRequest;
import com.globalegrow.dy.dto.SkuResponse;
import com.globalegrow.dy.service.RealtimeRecommendationProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * @Description 实时推荐数据分析-商品维度
 * @Author chongzi
 * @Date 2018/10/16 9:02
 **/
@RestController
@RequestMapping("sku")
public class RealtimeRecommendationProductController {

    @Autowired
    private RealtimeRecommendationProductService realtimeRecommendationProductService;

    @RequestMapping(value = "getSkuInfo", method = RequestMethod.POST)
    public Map<String, Object> getSkuInfo(@RequestBody SkuRequest skuRequest)throws IOException {
        return realtimeRecommendationProductService.getSkuInfo(skuRequest);
    }

}
