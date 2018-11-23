package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.SkuRequest;
import com.globalegrow.dy.dto.SkuResponse;

import java.io.IOException;
import java.util.Map;

/**
 * @Description 实时推荐数据分析-商品维度
 * @Author chongzi
 * @Date 2018/10/10 9:41
 **/
public interface RealtimeRecommendationProductService {
    /**
     * @Description 统计每天商品点击事件数量
     * @Author chongzi
     * @Date 2018/10/10 9:42
     **/
    Map<String, Object> getSkuInfo(SkuRequest skuRequest) throws IOException;


}
