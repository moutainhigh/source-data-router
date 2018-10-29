package com.globalegrow.dy.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@Deprecated
public class UserActionDto {

    //private Double gmv = 0d;

    private String userId;

    private String cookieId;

    private String site;
    // 订单事件
    private List<UserActionData> orders;
    // 商品曝光
    private List<UserActionData> skuExposure;
    // 商品收藏
    private List<UserActionData> skuMarked;
    // 商品加购
    private List<UserActionData> skuCart;
    // 商品点击
    private List<UserActionData> skuHit;
    // 商品搜索
    private List<UserActionData> skuSearchWord;
    // 订单支付成功
    private List<UserActionData> skuOrderPay;

    public List<UserActionData> getSkuOrderPay() {
        return skuOrderPay;
    }

    public void setSkuOrderPay(List<UserActionData> skuOrderPay) {
        this.skuOrderPay = skuOrderPay;
    }

    public List<UserActionData> getSkuExposure() {
        return skuExposure;
    }

    public void setSkuExposure(List<UserActionData> skuExposure) {
        this.skuExposure = skuExposure;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public List<UserActionData> getOrders() {
        return orders;
    }

    public void setOrders(List<UserActionData> orders) {
        this.orders = orders;
    }

    public List<UserActionData> getSkuMarked() {
        return skuMarked;
    }

    public void setSkuMarked(List<UserActionData> skuMarked) {
        this.skuMarked = skuMarked;
    }

    public List<UserActionData> getSkuCart() {
        return skuCart;
    }

    public void setSkuCart(List<UserActionData> skuCart) {
        this.skuCart = skuCart;
    }

    public List<UserActionData> getSkuHit() {
        return skuHit;
    }

    public void setSkuHit(List<UserActionData> skuHit) {
        this.skuHit = skuHit;
    }

    public List<UserActionData> getSkuSearchWord() {
        return skuSearchWord;
    }

    public void setSkuSearchWord(List<UserActionData> skuSearchWord) {
        this.skuSearchWord = skuSearchWord;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("cookieId", cookieId)
                .append("site", site)
                .append("orders", orders)
                .append("skuExposure", skuExposure)
                .append("skuMarked", skuMarked)
                .append("skuCart", skuCart)
                .append("skuHit", skuHit)
                .append("skuSearchWord", skuSearchWord)
                .append("skuOrderPay", skuOrderPay)
                .toString();
    }
}
