package com.globalegrow.dy.bts.model;

import com.globalegrow.bts.model.BtsReport;

public class SkuCartInfo extends BtsReport {

    private String sku;

    private String userId;

    private String deviceId;
    /**
     * 是否是推荐位
     */
    private Boolean recommend;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public Boolean getRecommend() {
        return recommend;
    }

    public void setRecommend(Boolean recommend) {
        this.recommend = recommend;
    }
}
