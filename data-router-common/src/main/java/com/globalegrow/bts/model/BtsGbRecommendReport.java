package com.globalegrow.bts.model;

import com.globalegrow.bts.enums.DefaultFields;

public class BtsGbRecommendReport extends BtsReport{
    // 商详页 pv
    private int goodDetailPv = 0;
    // 商详页 uv
    private String goodDetailUv = DefaultFields._skip.name();
    // 推荐位 pv
    private int recommendTypeExposurePv = 0;
    // sku 曝光数
    private int skuExposure = 0;
    // sku 点击数
    private int skuClick = 0;
    // sku 加购数
    private int skuAddCart = 0;
    // sku 下单数
    private int skuOrder = 0;
    // sku 下单支付数
    private int skuOrderPaid = 0;
    // 支付金额
    private int amount;

    public int getGoodDetailPv() {
        return goodDetailPv;
    }

    public void setGoodDetailPv(int goodDetailPv) {
        this.goodDetailPv = goodDetailPv;
    }

    public String getGoodDetailUv() {
        return goodDetailUv;
    }

    public void setGoodDetailUv(String goodDetailUv) {
        this.goodDetailUv = goodDetailUv;
    }

    public int getRecommendTypeExposurePv() {
        return recommendTypeExposurePv;
    }

    public void setRecommendTypeExposurePv(int recommendTypeExposurePv) {
        this.recommendTypeExposurePv = recommendTypeExposurePv;
    }

    public int getSkuExposure() {
        return skuExposure;
    }

    public void setSkuExposure(int skuExposure) {
        this.skuExposure = skuExposure;
    }

    public int getSkuClick() {
        return skuClick;
    }

    public void setSkuClick(int skuClick) {
        this.skuClick = skuClick;
    }

    public int getSkuAddCart() {
        return skuAddCart;
    }

    public void setSkuAddCart(int skuAddCart) {
        this.skuAddCart = skuAddCart;
    }

    public int getSkuOrder() {
        return skuOrder;
    }

    public void setSkuOrder(int skuOrder) {
        this.skuOrder = skuOrder;
    }

    public int getSkuOrderPaid() {
        return skuOrderPaid;
    }

    public void setSkuOrderPaid(int skuOrderPaid) {
        this.skuOrderPaid = skuOrderPaid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
