package com.globalegrow.bts.report;

import java.util.HashMap;
import java.util.Map;

public class ZafulPcRecommendReportModel {


    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    private String specimen = "_skip";//样本量

    private int good_exp_pv = 0;//商品(SKU)曝光
    private String good_exp_uv="_skip";//查看商品UV

    private int good_cli_pv = 0;//商品点击量
    private String good_cli_uv="_skip";//点击用户量

    private int good_cart_num = 0;//商品加购次数
    private String good_cart_uv="_skip";//加购用户数

    private int good_order_num = 0;//下单商品数
    private String good_order_uv="_skip";//下单客户数
    private int order_amount=0;//下单金额

    private int sales_amount = 0;//付款商品数
    private String sales_uv="_skip";//付款客户数

    private int gmv = 0;//付款金额

    private int good_collect_num=0;//商品加收次数
    private String good_collect_uv="_skip";//加收用户数
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

    public int getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(int order_amount) {
        this.order_amount = order_amount;
    }

    public int getSales_amount() {
        return sales_amount;
    }

    public void setSales_amount(int sales_amount) {
        this.sales_amount = sales_amount;
    }

    public String getSales_uv() {
        return sales_uv;
    }

    public void setSales_uv(String sales_uv) {
        this.sales_uv = sales_uv;
    }

    public int getGmv() {
        return gmv;
    }

    public void setGmv(int gmv) {
        this.gmv = gmv;
    }

    public int getGood_collect_num() {
        return good_collect_num;
    }

    public void setGood_collect_num(int good_collect_num) {
        this.good_collect_num = good_collect_num;
    }

    public String getGood_collect_uv() {
        return good_collect_uv;
    }

    public void setGood_collect_uv(String good_collect_uv) {
        this.good_collect_uv = good_collect_uv;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
