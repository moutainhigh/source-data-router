package com.globalegrow.bts.model;

import java.util.Map;

public class BtsZafulListPageReport {

    private static final String skip = "_skip";

    private String listPageUv = skip;

    private String listPageRecommendUv = skip;

    private String specimen = skip;

    private Integer exposure = 0;

    private Integer goodClick = 0;

    private Integer addCart = 0;

    private Integer salesVolume = 0;

    private Integer salesAmount = 0;

    private Long timestamp = System.currentTimeMillis();

    private Map<String, String> bts;

    public String getListPageRecommendUv() {
        return listPageRecommendUv;
    }

    public void setListPageRecommendUv(String listPageRecommendUv) {
        this.listPageRecommendUv = listPageRecommendUv;
    }

    public String getSkip() {
        return skip;
    }

    public String getListPageUv() {
        return listPageUv;
    }

    public void setListPageUv(String listPageUv) {
        this.listPageUv = listPageUv;
    }

    public String getSpecimen() {
        return specimen;
    }

    public void setSpecimen(String specimen) {
        this.specimen = specimen;
    }

    public Integer getExposure() {
        return exposure;
    }

    public void setExposure(Integer exposure) {
        this.exposure = exposure;
    }

    public Integer getGoodClick() {
        return goodClick;
    }

    public void setGoodClick(Integer goodClick) {
        this.goodClick = goodClick;
    }

    public Integer getAddCart() {
        return addCart;
    }

    public void setAddCart(Integer addCart) {
        this.addCart = addCart;
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

    public Map<String, String> getBts() {
        return bts;
    }

    public void setBts(Map<String, String> bts) {
        this.bts = bts;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
