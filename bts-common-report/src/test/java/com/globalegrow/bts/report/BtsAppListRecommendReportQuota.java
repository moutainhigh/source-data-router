package com.globalegrow.bts.report;

import com.globalegrow.bts.model.BtsReport;

/**
 * app分类页推荐报表指标字段
 */
public class BtsAppListRecommendReportQuota extends BtsReport {
    private int exposure_count = 0;//商品(SKU)曝光

    private int good_click = 0;//点击量

    private String good_click_uv = skip;//点击用户量

    private int good_add_cart = 0;//商品加购数量

    private String good_add_cart_uv = skip;//加购用户数

    private String good_collect_uv = skip;//加收用户数

    private int good_collect = 0;//商品加收次数

    private int order_sku = 0;//下单商品数
    /**
     * GMV 生单金额
     */
    private int order_amount = 0;//下单金额
    /**
     * 生单用户数
     */
    private String order_uv = skip;//下单客户数
    /**
     * 支付用户数
     */
    private String paid_uv = skip;//付款客户数
    /**
     * 销售额
     */
    private int amount = 0;//付款金额
    // 销量
    private int sales_amount = 0;//付款商品数
    private long timestamp = System.currentTimeMillis();

    public int getExposure_count() {
        return exposure_count;
    }

    public void setExposure_count(int exposure_count) {
        this.exposure_count = exposure_count;
    }

    public int getGood_click() {
        return good_click;
    }

    public void setGood_click(int good_click) {
        this.good_click = good_click;
    }

    public String getGood_click_uv() {
        return good_click_uv;
    }

    public void setGood_click_uv(String good_click_uv) {
        this.good_click_uv = good_click_uv;
    }

    public int getGood_add_cart() {
        return good_add_cart;
    }

    public void setGood_add_cart(int good_add_cart) {
        this.good_add_cart = good_add_cart;
    }

    public String getGood_add_cart_uv() {
        return good_add_cart_uv;
    }

    public void setGood_add_cart_uv(String good_add_cart_uv) {
        this.good_add_cart_uv = good_add_cart_uv;
    }

    public String getGood_collect_uv() {
        return good_collect_uv;
    }

    public void setGood_collect_uv(String good_collect_uv) {
        this.good_collect_uv = good_collect_uv;
    }

    public int getGood_collect() {
        return good_collect;
    }

    public void setGood_collect(int good_collect) {
        this.good_collect = good_collect;
    }

    public int getOrder_sku() {
        return order_sku;
    }

    public void setOrder_sku(int order_sku) {
        this.order_sku = order_sku;
    }

    public int getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(int order_amount) {
        this.order_amount = order_amount;
    }

    public String getOrder_uv() {
        return order_uv;
    }

    public void setOrder_uv(String order_uv) {
        this.order_uv = order_uv;
    }

    public String getPaid_uv() {
        return paid_uv;
    }

    public void setPaid_uv(String paid_uv) {
        this.paid_uv = paid_uv;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getSales_amount() {
        return sales_amount;
    }

    public void setSales_amount(int sales_amount) {
        this.sales_amount = sales_amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
