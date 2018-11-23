package com.globalegrow.bts.report;

import java.util.HashMap;
import java.util.Map;

public class SearchRecommendReportQuotaModel {


    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    private String specimen = "_skip";

    private int search_rec_pv = 0;

    private String search_rec_uv = "_skip";

    private int good_exp_pv = 0;

    private String good_exp_uv = "_skip";

    private int good_cli_pv = 0;

    private String good_cli_uv = "_skip";

    private int good_cart_num = 0;

    private String good_cart_uv = "_skip";

    private int good_order_num = 0;

    private String good_order_uv = "_skip";

    private int gmv = 0;

    private int good_paid_num = 0;

    private String pay_uv = "_skip";

    private int sales_amount = 0;

    private Long timestamp = 0L;

    public String getPay_uv() {
        return pay_uv;
    }

    public void setPay_uv(String pay_uv) {
        this.pay_uv = pay_uv;
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

    public int getSearch_rec_pv() {
        return search_rec_pv;
    }

    public void setSearch_rec_pv(int search_rec_pv) {
        this.search_rec_pv = search_rec_pv;
    }

    public String getSearch_rec_uv() {
        return search_rec_uv;
    }

    public void setSearch_rec_uv(String search_rec_uv) {
        this.search_rec_uv = search_rec_uv;
    }

    public int getGood_exp_pv() {
        return good_exp_pv;
    }

    public void setGood_exp_pv(int good_exp_pv) {
        this.good_exp_pv = good_exp_pv;
    }

    public String getGood_exp_uv() {
        return good_exp_uv;
    }

    public void setGood_exp_uv(String good_exp_uv) {
        this.good_exp_uv = good_exp_uv;
    }

    public int getGood_cli_pv() {
        return good_cli_pv;
    }

    public void setGood_cli_pv(int good_cli_pv) {
        this.good_cli_pv = good_cli_pv;
    }

    public String getGood_cli_uv() {
        return good_cli_uv;
    }

    public void setGood_cli_uv(String good_cli_uv) {
        this.good_cli_uv = good_cli_uv;
    }

    public int getGood_cart_num() {
        return good_cart_num;
    }

    public void setGood_cart_num(int good_cart_num) {
        this.good_cart_num = good_cart_num;
    }

    public String getGood_cart_uv() {
        return good_cart_uv;
    }

    public void setGood_cart_uv(String good_cart_uv) {
        this.good_cart_uv = good_cart_uv;
    }

    public int getGood_order_num() {
        return good_order_num;
    }

    public void setGood_order_num(int good_order_num) {
        this.good_order_num = good_order_num;
    }

    public String getGood_order_uv() {
        return good_order_uv;
    }

    public void setGood_order_uv(String good_order_uv) {
        this.good_order_uv = good_order_uv;
    }

    public int getGmv() {
        return gmv;
    }

    public void setGmv(int gmv) {
        this.gmv = gmv;
    }

    public int getGood_paid_num() {
        return good_paid_num;
    }

    public void setGood_paid_num(int good_paid_num) {
        this.good_paid_num = good_paid_num;
    }

    public int getSales_amount() {
        return sales_amount;
    }

    public void setSales_amount(int sales_amount) {
        this.sales_amount = sales_amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
