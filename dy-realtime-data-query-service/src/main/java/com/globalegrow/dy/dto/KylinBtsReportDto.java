package com.globalegrow.dy.dto;

import java.util.Date;

public class KylinBtsReportDto {

    private Long pv = 0L;

    private Long uv = 0L;

    private Long specimenCount = 0L;

    private Long exposureCount = 0L;

    private Long clickCount = 0L;

    private Long addCartCount = 0L;

    private Long skuOrderCount = 0L;

    private Long paidOrderCount = 0L;

    private Float paidAmount = 0F;
    // 曝光点击率
    private Float expClickRate = 0F;
    // 加购率
    private Float addCartRate = 0F;
    // 下单转化率
    private Float orderRate = 0F;
    // 购买转化率
    private Float paidOrderRate = 0F;
    // 总体转化率
    private Float totalRate = 0F;

    private String btsPlanId;
    private String btsVersionId;
    private String btsBucketId;
    private Date dayStart;

    public Long getPv() {
        return pv;
    }

    public void setPv(Long pv) {
        this.pv = pv;
    }

    public Long getUv() {
        return uv;
    }

    public void setUv(Long uv) {
        this.uv = uv;
    }

    public Long getSpecimenCount() {
        return specimenCount;
    }

    public void setSpecimenCount(Long specimenCount) {
        this.specimenCount = specimenCount;
    }

    public Long getExposureCount() {
        return exposureCount;
    }

    public void setExposureCount(Long exposureCount) {
        this.exposureCount = exposureCount;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public Long getAddCartCount() {
        return addCartCount;
    }

    public void setAddCartCount(Long addCartCount) {
        this.addCartCount = addCartCount;
    }

    public Long getSkuOrderCount() {
        return skuOrderCount;
    }

    public void setSkuOrderCount(Long skuOrderCount) {
        this.skuOrderCount = skuOrderCount;
    }

    public Long getPaidOrderCount() {
        return paidOrderCount;
    }

    public void setPaidOrderCount(Long paidOrderCount) {
        this.paidOrderCount = paidOrderCount;
    }

    public Float getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Float paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Float getExpClickRate() {
        return expClickRate;
    }

    public void setExpClickRate(Float expClickRate) {
        this.expClickRate = expClickRate;
    }

    public Float getAddCartRate() {
        return addCartRate;
    }

    public void setAddCartRate(Float addCartRate) {
        this.addCartRate = addCartRate;
    }

    public Float getOrderRate() {
        return orderRate;
    }

    public void setOrderRate(Float orderRate) {
        this.orderRate = orderRate;
    }

    public Float getPaidOrderRate() {
        return paidOrderRate;
    }

    public void setPaidOrderRate(Float paidOrderRate) {
        this.paidOrderRate = paidOrderRate;
    }

    public Float getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(Float totalRate) {
        this.totalRate = totalRate;
    }

    public String getBtsPlanId() {
        return btsPlanId;
    }

    public void setBtsPlanId(String btsPlanId) {
        this.btsPlanId = btsPlanId;
    }

    public String getBtsVersionId() {
        return btsVersionId;
    }

    public void setBtsVersionId(String btsVersionId) {
        this.btsVersionId = btsVersionId;
    }

    public String getBtsBucketId() {
        return btsBucketId;
    }

    public void setBtsBucketId(String btsBucketId) {
        this.btsBucketId = btsBucketId;
    }

    public Date getDayStart() {
        return dayStart;
    }

    public void setDayStart(Date dayStart) {
        this.dayStart = dayStart;
    }
}
