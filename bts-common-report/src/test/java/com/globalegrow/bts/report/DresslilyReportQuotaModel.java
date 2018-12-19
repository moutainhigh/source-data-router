package com.globalegrow.bts.report;

import java.util.HashMap;
import java.util.Map;

public class DresslilyReportQuotaModel {


    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    private String specimen = "_skip";

    private int search_page_pv = 0;

    private String search_page_uv = "_skip";

    private int cart_page_pv = 0;

    private String cart_page_uv = "_skip";

    private int quick_pay_pv = 0;

    private String quick_pay_uv = "_skip";

    private int buy_now_btn_click_pv = 0;

    private String buy_now_btn_click_uv = "_skip";

    private int add_cart_btn_click_pv = 0;

    private String add_cart_btn_click_uv = "_skip";

    private int order_page_click_pv = 0;

    private String order_page_click_uv = "_skip";

    private int place_order_btn_click_pv = 0;
    private String place_order_btn_click_uv="_skip";

    private Long timestamp = 0L;

    public Map<String, String> getBts() {
        return bts;
    }

    public void setBts(Map<String, String> bts) {
        this.bts = bts;
    }

    public String getSpecimen() {
        return specimen;
    }

    public void setSpecimen(String specimen) {
        this.specimen = specimen;
    }

    public int getSearch_page_pv() {
        return search_page_pv;
    }

    public void setSearch_page_pv(int search_page_pv) {
        this.search_page_pv = search_page_pv;
    }

    public String getSearch_page_uv() {
        return search_page_uv;
    }

    public void setSearch_page_uv(String search_page_uv) {
        this.search_page_uv = search_page_uv;
    }

    public int getCart_page_pv() {
        return cart_page_pv;
    }

    public void setCart_page_pv(int cart_page_pv) {
        this.cart_page_pv = cart_page_pv;
    }

    public String getCart_page_uv() {
        return cart_page_uv;
    }

    public void setCart_page_uv(String cart_page_uv) {
        this.cart_page_uv = cart_page_uv;
    }

    public int getQuick_pay_pv() {
        return quick_pay_pv;
    }

    public void setQuick_pay_pv(int quick_pay_pv) {
        this.quick_pay_pv = quick_pay_pv;
    }

    public String getQuick_pay_uv() {
        return quick_pay_uv;
    }

    public void setQuick_pay_uv(String quick_pay_uv) {
        this.quick_pay_uv = quick_pay_uv;
    }

    public int getBuy_now_btn_click_pv() {
        return buy_now_btn_click_pv;
    }

    public void setBuy_now_btn_click_pv(int buy_now_btn_click_pv) {
        this.buy_now_btn_click_pv = buy_now_btn_click_pv;
    }

    public String getBuy_now_btn_click_uv() {
        return buy_now_btn_click_uv;
    }

    public void setBuy_now_btn_click_uv(String buy_now_btn_click_uv) {
        this.buy_now_btn_click_uv = buy_now_btn_click_uv;
    }

    public int getAdd_cart_btn_click_pv() {
        return add_cart_btn_click_pv;
    }

    public void setAdd_cart_btn_click_pv(int add_cart_btn_click_pv) {
        this.add_cart_btn_click_pv = add_cart_btn_click_pv;
    }

    public String getAdd_cart_btn_click_uv() {
        return add_cart_btn_click_uv;
    }

    public void setAdd_cart_btn_click_uv(String add_cart_btn_click_uv) {
        this.add_cart_btn_click_uv = add_cart_btn_click_uv;
    }

    public int getOrder_page_click_pv() {
        return order_page_click_pv;
    }

    public void setOrder_page_click_pv(int order_page_click_pv) {
        this.order_page_click_pv = order_page_click_pv;
    }

    public String getOrder_page_click_uv() {
        return order_page_click_uv;
    }

    public void setOrder_page_click_uv(String order_page_click_uv) {
        this.order_page_click_uv = order_page_click_uv;
    }

    public int getPlace_order_btn_click_pv() {
        return place_order_btn_click_pv;
    }

    public void setPlace_order_btn_click_pv(int place_order_btn_click_pv) {
        this.place_order_btn_click_pv = place_order_btn_click_pv;
    }

    public String getPlace_order_btn_click_uv() {
        return place_order_btn_click_uv;
    }

    public void setPlace_order_btn_click_uv(String place_order_btn_click_uv) {
        this.place_order_btn_click_uv = place_order_btn_click_uv;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
