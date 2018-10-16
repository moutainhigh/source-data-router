package com.globalegrow.dy.dto;

import java.util.Date;
import java.util.List;

/**
 * @Description 获取商品特征接口查询条件DTO
 * @Author chongzi
 * @Date 2018/10/15 20:18
 **/
public class SkuRequest {

    /**
     * 商品id，查询全部商品特征时不需要填写
     */
    private String sku;

    /**
     * 起始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 网站源，zaful, gb等，可多选
     */
    private String site;

    /**
     * 终端类型，pc, m, app，多选
     */
    private String divice;

    /**
     * 分页ID
     */
    private String scrollId;

    /**
     * 每页数量
     */
    private Integer size;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDivice() {
        return divice;
    }

    public void setDivice(String divice) {
        this.divice = divice;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
