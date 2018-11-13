package com.globalegrow.report.order;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ReportOrderInfo {
    // 订单商品表 id
    private Integer order_goods_id = 0;

    private int goods_num = 0;

    private Integer order_id;

    private String user_id;

    private String sku;

    // 商品表中支付金额字段
    private long gmv = 0;
    // 单价 * 数量金额
    private long amount_num_price = 0;

    private String order_status = "-1";
    /**
     * 是否已处理过订单
     */
    private Boolean hasSent = false;

    private Boolean orderData = false;

    public Boolean getOrderData() {
        return orderData;
    }

    public void setOrderData(Boolean orderData) {
        this.orderData = orderData;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }

    public int getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(int goods_num) {
        this.goods_num = goods_num;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getGmv() {
        return gmv;
    }

    public void setGmv(long gmv) {
        this.gmv = gmv;
    }

    public long getAmount_num_price() {
        return amount_num_price;
    }

    public void setAmount_num_price(long amount_num_price) {
        this.amount_num_price = amount_num_price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public Boolean getHasSent() {
        return hasSent;
    }

    public void setHasSent(Boolean hasSent) {
        this.hasSent = hasSent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("order_goods_id", order_goods_id)
                .append("goods_num", goods_num)
                .append("order_id", order_id)
                .append("user_id", user_id)
                .append("gmv", gmv)
                .append("amount_num_price", amount_num_price)
                .append("order_status", order_status)
                .append("hasSent", hasSent)
                .toString();
    }
}
