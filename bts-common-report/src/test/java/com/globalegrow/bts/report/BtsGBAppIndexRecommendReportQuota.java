package com.globalegrow.bts.report;

/**
 * GB app 首页瀑布流推荐报表指标字段
 */
public class BtsGBAppIndexRecommendReportQuota extends BtsReport {
    private int exposure_count = 0;//商品曝光数

    private String exposure_uv=skip;//查看商品UV

    private int good_click = 0;//商品点击数

    private String good_click_uv = skip;//点击UV

    private int good_add_cart = 0;//商品加购数

    private String good_add_cart_uv = skip;//加购UV

    private String good_collect_uv = skip;//商品收藏UV

    private int good_collect = 0;//商品收藏数

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

    public String getExposure_uv() {
        return exposure_uv;
    }

    public void setExposure_uv(String exposure_uv) {
        this.exposure_uv = exposure_uv;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
