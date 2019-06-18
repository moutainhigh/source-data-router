package com.globalegrow.dy.goods.info.service.impl;

import com.globalegrow.dy.dto.CommonMapResponse;
import com.globalegrow.dy.goods.info.dto.GoodsExtendInfoRequest;
import com.globalegrow.dy.goods.info.service.GoodsExtendInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Data
public class GoodsExtendInfoServiceEsImpl implements GoodsExtendInfoService {

    @Autowired
    private TransportClient client;

    @Override
    public CommonMapResponse getGoodsExtendInfo(GoodsExtendInfoRequest request) {
        Map<String, Object> goodInfos = new HashMap<>();

        return null;
    }
}
