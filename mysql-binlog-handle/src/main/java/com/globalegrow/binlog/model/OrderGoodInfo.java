package com.globalegrow.binlog.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderGoodInfo implements Serializable {

    public static final String ORDER_ID = "order_id";
    public static final String SKU = "goods_sn";
    public static final String PRICE = "goods_price";
    public static final String GOODS_NUM = "goods_number";

    private String orderId;

    private String sku;

    private Float price;

    private Integer goodsNum;

    private Integer amount = 0;

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Integer getAmount() {
        if (this.amount != null && this.amount > 0) {
            return this.amount;
        }
        if (this.price != null && this.goodsNum != null) {
            Float f = this.price  * 100 * this.goodsNum;
            return f.intValue();
        }
        return 0;
    }
}
