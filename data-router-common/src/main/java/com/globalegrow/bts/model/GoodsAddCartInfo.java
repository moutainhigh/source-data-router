package com.globalegrow.bts.model;

import java.util.Map;

/**
 * 商品加购信息
 */
public class GoodsAddCartInfo {

    private String cookie;

    private String userId;

    private String sku;
    /**
     * 加购数量
     */
    private Integer pam;

    private Integer salesVolume = 0;

    private Integer salesAmount = 0;

    private Map<String, String> bts;

    public GoodsAddCartInfo() {
    }

    public GoodsAddCartInfo(String cookie, String userId, String sku, Integer pam, Map<String, String> bts) {
        this.cookie = cookie;
        this.userId = userId;
        this.sku = sku;
        this.pam = pam;
        this.bts = bts;
    }

    public Map<String, String> getBts() {
        return bts;
    }

    public void setBts(Map<String, String> bts) {
        this.bts = bts;
    }

    public Integer getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(Integer salesVolume) {
        this.salesVolume = salesVolume;
    }

    public Integer getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(Integer salesAmount) {
        this.salesAmount = salesAmount;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getPam() {
        return pam;
    }

    public void setPam(Integer pam) {
        this.pam = pam;
    }
}
