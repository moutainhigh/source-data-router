package com.globalegrow.bts.report.model;

import com.globalegrow.bts.report.BtsReport;
import com.globalegrow.bts.report.DefaultFields;

public class CommonReportWithBuryLog extends BtsReport {

    private int pv = 0;

    private String uv = DefaultFields._skip.name();

    private int exp_num = 0;

    private String exp_uv = DefaultFields._skip.name();

    private int sku_click_num = 0;

    private String sku_click_uv = DefaultFields._skip.name();

    private int sku_cart_num = 0;

    private String sku_cart_uv = DefaultFields._skip.name();

    private int sku_collect_num = 0;

    private String sku_collect_uv = DefaultFields._skip.name();


    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }

    public int getExp_num() {
        return exp_num;
    }

    public void setExp_num(int exp_num) {
        this.exp_num = exp_num;
    }

    public String getExp_uv() {
        return exp_uv;
    }

    public void setExp_uv(String exp_uv) {
        this.exp_uv = exp_uv;
    }

    public int getSku_click_num() {
        return sku_click_num;
    }

    public void setSku_click_num(int sku_click_num) {
        this.sku_click_num = sku_click_num;
    }

    public String getSku_click_uv() {
        return sku_click_uv;
    }

    public void setSku_click_uv(String sku_click_uv) {
        this.sku_click_uv = sku_click_uv;
    }

    public int getSku_cart_num() {
        return sku_cart_num;
    }

    public void setSku_cart_num(int sku_cart_num) {
        this.sku_cart_num = sku_cart_num;
    }

    public String getSku_cart_uv() {
        return sku_cart_uv;
    }

    public void setSku_cart_uv(String sku_cart_uv) {
        this.sku_cart_uv = sku_cart_uv;
    }

    public int getSku_collect_num() {
        return sku_collect_num;
    }

    public void setSku_collect_num(int sku_collect_num) {
        this.sku_collect_num = sku_collect_num;
    }

    public String getSku_collect_uv() {
        return sku_collect_uv;
    }

    public void setSku_collect_uv(String sku_collect_uv) {
        this.sku_collect_uv = sku_collect_uv;
    }
}
