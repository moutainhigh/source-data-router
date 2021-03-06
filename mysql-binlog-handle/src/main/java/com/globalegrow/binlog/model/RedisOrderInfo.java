package com.globalegrow.binlog.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RedisOrderInfo implements Serializable {

    private OrderInfo orderInfo = new OrderInfo();

    private Map<String, OrderGoodInfo> goodInfoMap = new HashMap<>();

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public Map<String, OrderGoodInfo> getGoodInfoMap() {
        return goodInfoMap;
    }

    public void setGoodInfoMap(Map<String, OrderGoodInfo> goodInfoMap) {
        this.goodInfoMap = goodInfoMap;
    }
}
