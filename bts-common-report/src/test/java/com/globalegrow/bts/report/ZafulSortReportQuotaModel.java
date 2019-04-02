package com.globalegrow.bts.report;/**
 * Created by tangliuyi on 2019/3/7.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ZafulSortReportQuotaModel
 * @Description TODO
 * @Author tangliuyi
 * @Date 2019/3/7 15:16
 * @Version 1.0
 */
public class ZafulSortReportQuotaModel {
    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };
    private String specimen = "_skip";
    protected static final String skip = "_skip";

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

    public String getExposure_uv() {
        return exposure_uv;
    }

    public void setExposure_uv(String exposure_uv) {
        this.exposure_uv = exposure_uv;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

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
}
