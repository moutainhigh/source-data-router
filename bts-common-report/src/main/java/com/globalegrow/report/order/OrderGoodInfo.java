package com.globalegrow.report.order;

import java.io.Serializable;

public class OrderGoodInfo implements Serializable {

    public static final String ORDER_ID = "order_id";
    public static final String SKU = "goods_sn";
    public static final String PRICE = "goods_price";
    public static final String GOODS_NUM = "goods_number";
    private static final long serialVersionUID = 2386895577015735850L;

    private String orderId;

    private String userId;

    private String sku;

    private Float price = 0F;

    private Integer goodsNum = 0;

    private Integer amount = 0;

    private Integer gmv;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Integer getGmv() {
        return gmv;
    }

    public void setGmv(Integer gmv) {
        this.gmv = gmv;
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
