package com.globalegrow.dy.dto;

import java.util.Date;
import java.util.List;

/**
 * @Description 获取商品特征接口返回实体
 * @Author chongzi
 * @Date 2018/10/15 20:18
 **/
public class SkuResponse {

    /**
     * 商品id，查询全部商品特征时不需要填写
     */
    private String sku;
    /**
     * 消费总额
     */
    private Double gmv;
    /**
     * 商品被曝光数量
     */
    private Integer skuExpCnt;
    /**
     * 商品被点击数量
     */
    private Integer skuHitCnt;
    /**
     * 商品被加购物车数量
     */
    private Integer skuCartCnt;
    /**
     * 商品被加收藏数量
     */
    private Integer skuMarkedCnt;
    /**
     * 创建订单总数
     */
    private Integer createOrderCnt;
    /**
     * 购买订单总数
     */
    private Integer purchaseOrderCnt;
    /**
     * 订单总数
     */
    private Integer orderCnt;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getGmv() {
        return gmv;
    }

    public void setGmv(Double gmv) {
        this.gmv = gmv;
    }

    public Integer getSkuExpCnt() {
        return skuExpCnt;
    }

    public void setSkuExpCnt(Integer skuExpCnt) {
        this.skuExpCnt = skuExpCnt;
    }

    public Integer getSkuHitCnt() {
        return skuHitCnt;
    }

    public void setSkuHitCnt(Integer skuHitCnt) {
        this.skuHitCnt = skuHitCnt;
    }

    public Integer getSkuCartCnt() {
        return skuCartCnt;
    }

    public void setSkuCartCnt(Integer skuCartCnt) {
        this.skuCartCnt = skuCartCnt;
    }

    public Integer getSkuMarkedCnt() {
        return skuMarkedCnt;
    }

    public void setSkuMarkedCnt(Integer skuMarkedCnt) {
        this.skuMarkedCnt = skuMarkedCnt;
    }

    public Integer getCreateOrderCnt() {
        return createOrderCnt;
    }

    public void setCreateOrderCnt(Integer createOrderCnt) {
        this.createOrderCnt = createOrderCnt;
    }

    public Integer getPurchaseOrderCnt() {
        return purchaseOrderCnt;
    }

    public void setPurchaseOrderCnt(Integer purchaseOrderCnt) {
        this.purchaseOrderCnt = purchaseOrderCnt;
    }

    public Integer getOrderCnt() {
        return orderCnt;
    }

    public void setOrderCnt(Integer orderCnt) {
        this.orderCnt = orderCnt;
    }
}
