package com.globalegrow.bts.report;/**
 * Created by tangliuyi on 2019/1/9.
 */


/**
 * @ClassName GbSearchReportQuota
 * @Description TODO
 * @Author tangliuyi
 * @Date 2019/1/9 11:09
 * @Version 1.0
 */
public class GbSearchReportQuota extends BtsReport {
    /**
     * 样本量
     */
    private String specimen = "_skip";
    /**
     * 搜索页relevance PV
     */
    private int relevance_pv=0;
    /**
     * 搜索页relevance UV
     */
    private String relevance_uv="_skip";
    /**
     * 商品曝光 PV
     */
    private int exp_pv=0;
    /**
     * 商品曝光 UV
     */
    private String exp_uv="_skip";
    /**
     * 商品点击 PV
     */
    private int goods_click_pv=0;
    /**
     * 商品点击 UV
     */
    private String goods_click_uv="_skip";
    /**
     * 商品加购次数
     */
    private int goods_car_pv=0;
    /**
     * 加购UV
     */
    private String goods_car_uv="_skip";
    /**
     * 商品加购率
     */
    private String goods_add_rate="_skip";
    /**
     *用户加购率
     */
    private String user_add_rate="_skip";
    /**
     * 曝光点击率
     */
    private  String exp_rate="_skip";
    /**
     * 用户点击率
     */
    private String user_click_rate="_skip";

    @Override
    public String getSpecimen() {
        return specimen;
    }

    @Override
    public void setSpecimen(String specimen) {
        this.specimen = specimen;
    }

    public int getRelevance_pv() {
        return relevance_pv;
    }

    public void setRelevance_pv(int relevance_pv) {
        this.relevance_pv = relevance_pv;
    }

    public String getRelevance_uv() {
        return relevance_uv;
    }

    public void setRelevance_uv(String relevance_uv) {
        this.relevance_uv = relevance_uv;
    }

    public int getExp_pv() {
        return exp_pv;
    }

    public void setExp_pv(int exp_pv) {
        this.exp_pv = exp_pv;
    }

    public String getExp_uv() {
        return exp_uv;
    }

    public void setExp_uv(String exp_uv) {
        this.exp_uv = exp_uv;
    }

    public int getGoods_click_pv() {
        return goods_click_pv;
    }

    public void setGoods_click_pv(int goods_click_pv) {
        this.goods_click_pv = goods_click_pv;
    }

    public String getGoods_click_uv() {
        return goods_click_uv;
    }

    public void setGoods_click_uv(String goods_click_uv) {
        this.goods_click_uv = goods_click_uv;
    }

    public int getGoods_car_pv() {
        return goods_car_pv;
    }

    public void setGoods_car_pv(int goods_car_pv) {
        this.goods_car_pv = goods_car_pv;
    }

    public String getGoods_car_uv() {
        return goods_car_uv;
    }

    public void setGoods_car_uv(String goods_car_uv) {
        this.goods_car_uv = goods_car_uv;
    }

    public String getGoods_add_rate() {
        return goods_add_rate;
    }

    public void setGoods_add_rate(String goods_add_rate) {
        this.goods_add_rate = goods_add_rate;
    }

    public String getUser_add_rate() {
        return user_add_rate;
    }

    public void setUser_add_rate(String user_add_rate) {
        this.user_add_rate = user_add_rate;
    }

    public String getExp_rate() {
        return exp_rate;
    }

    public void setExp_rate(String exp_rate) {
        this.exp_rate = exp_rate;
    }

    public String getUser_click_rate() {
        return user_click_rate;
    }

    public void setUser_click_rate(String user_click_rate) {
        this.user_click_rate = user_click_rate;
    }
}
