package com.globalegrow.report.order;

public class OrderInfo {

    private int goods_num = 0;

    private Integer order_id;

    private String user_id;

    private long gmv = 0;

    private long amount_num_price = 0;

    private long amount_pay = 0;

    private String order_status;

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

    public long getAmount_pay() {
        return amount_pay;
    }

    public void setAmount_pay(long amount_pay) {
        this.amount_pay = amount_pay;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}
