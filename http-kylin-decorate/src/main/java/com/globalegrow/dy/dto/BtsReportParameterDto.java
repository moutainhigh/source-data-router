package com.globalegrow.dy.dto;

import com.globalegrow.dy.utils.MD5CipherUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BtsReportParameterDto implements Serializable {
    private static final long serialVersionUID = 5311287586975858122L;
    // 实验 id
    private Long planId;
    // 分组字段
    private List<String> groupByFields = new ArrayList<>();
    // where = 条件
    private Map<String, String> whereFields = new HashMap<>();
    // 范围查询条件，between and
    private Map<String, Map<String, String>> betweenFields = new HashMap<>();
    // 排序字段，默认 day_start 降序
    private Map<String, String> orderFields = new HashMap<>();
    // 起始页，默认 1 ，传 0 为不分页
    private Integer startPage = 1;
    // 分页时，每页数据量
    private Integer pageSize = 10;
    // 类型，query/export 查询 or 导出源数据
    private String type;
    // bts 产品线
    private String productLineCode;
    // 实验 code
    private String planCode;

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getProductLineCode() {
        return productLineCode;
    }

    public void setProductLineCode(String productLineCode) {
        this.productLineCode = productLineCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<String> getGroupByFields() {
        return groupByFields;
    }

    public void setGroupByFields(List<String> groupByFields) {
        this.groupByFields = groupByFields;
    }

    public Map<String, String> getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(Map<String, String> whereFields) {
        this.whereFields = whereFields;
    }

    public Map<String, Map<String, String>> getBetweenFields() {
        return betweenFields;
    }

    public void setBetweenFields(Map<String, Map<String, String>> betweenFields) {
        this.betweenFields = betweenFields;
    }

    public Map<String, String> getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(Map<String, String> orderFields) {
        this.orderFields = orderFields;
    }

    public Integer getStartPage() {
        return startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "BtsReportParameterDto{" +
                "planId=" + planId +
                ", groupByFields=" + groupByFields +
                ", whereFields=" + whereFields +
                ", betweenFields=" + betweenFields +
                ", orderFields=" + orderFields +
                ", startPage=" + startPage +
                ", pageSize=" + pageSize +
                ", type='" + type + '\'' +
                ", productLineCode='" + productLineCode + '\'' +
                '}';
    }

    public String getCacheKey() {
        return MD5CipherUtil.generatePassword(this.toString());
    }
}
