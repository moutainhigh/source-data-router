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
     * 查询日期类型(0:近5天，1:当月，2:近两月，3:近三月，6:近六月)
     */
    private Integer type;

    /**
     * 起始时间（查询日期类型为0时必填）
     */
    private String startDate;

    /**
     * 结束时间（查询日期类型为0时必填）
     */
    private String endDate;

    /**
     * 网站源，zaful, gb等，可多选
     */
    private List<String> site;

    /**
     * 终端类型，pc, m, app，多选
     */
    private List<String> divice;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public List<String> getSite() {
        return site;
    }

    public void setSite(List<String> site) {
        this.site = site;
    }

    public List<String> getDivice() {
        return divice;
    }

    public void setDivice(List<String> divice) {
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
