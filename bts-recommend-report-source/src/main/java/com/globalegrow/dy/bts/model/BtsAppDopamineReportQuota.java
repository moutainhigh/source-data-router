package com.globalegrow.dy.bts.model;

import com.globalegrow.bts.model.BtsReport;

/**
 * 多巴胺推荐报表指标字段
 */
public class BtsAppDopamineReportQuota extends BtsReport {

    private int exposure_count = 0;

    private String good_view_uv = skip;

    private int good_click = 0;

    private String good_click_uv = skip;

    private int good_add_cart = 0;

    private String good_add_cart_uv = skip;

    private String good_collect_uv = skip;

    private int good_collect = 0;

    private int order_sku = 0;
    /**
     * 生单用户数
     */
    private int order = 0;
    /**
     * 订单成交量
     */
    private int paid_order = 0;
    /**
     * GMV 生单金额
     */
    private int order_amount = 0;
    private String order_uv = skip;

    private String paid_uv = skip;
    /**
     * 销售额
     */
    private int amount = 0;
    // 销量
    private int sales_amount = 0;

    private String whole_paid_uv = skip;
    /**
     * 整体销售额
     */
    private int whole_amount = 0;

    private String whole_order_uv = skip;

    private int whole_order_amount = 0;

    public int getExposure_count() {
        return exposure_count;
    }

    public void setExposure_count(int exposure_count) {
        this.exposure_count = exposure_count;
    }

    public String getGood_view_uv() {
        return good_view_uv;
    }

    public void setGood_view_uv(String good_view_uv) {
        this.good_view_uv = good_view_uv;
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

    public String getWhole_paid_uv() {
        return whole_paid_uv;
    }

    public void setWhole_paid_uv(String whole_paid_uv) {
        this.whole_paid_uv = whole_paid_uv;
    }

    public int getWhole_amount() {
        return whole_amount;
    }

    public void setWhole_amount(int whole_amount) {
        this.whole_amount = whole_amount;
    }

    public String getWhole_order_uv() {
        return whole_order_uv;
    }

    public void setWhole_order_uv(String whole_order_uv) {
        this.whole_order_uv = whole_order_uv;
    }

    public int getWhole_order_amount() {
        return whole_order_amount;
    }

    public void setWhole_order_amount(int whole_order_amount) {
        this.whole_order_amount = whole_order_amount;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPaid_order() {
        return paid_order;
    }

    public void setPaid_order(int paid_order) {
        this.paid_order = paid_order;
    }

    public int getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(int order_amount) {
        this.order_amount = order_amount;
    }
}
