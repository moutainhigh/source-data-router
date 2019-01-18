package com.globalegrow.dy.dto;

import com.globalegrow.dy.utils.MD5CipherUtil;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsReportParameterDto implements Serializable {

    private static final long serialVersionUID = -4012435867239099915L;

    @NotNull(message = "报表id不能为NULL")
    private Long reportId;

    @NotEmpty(message = "网站代码不能为NULL")
    private String websiteCode;

    // 分组字段
    private List<String> groupByFields = new ArrayList<>();
    // where = 条件
    private Map<String, String> whereFields = new HashMap<>();
    // 范围查询条件，between and
    private Map<String, Map<String, String>> betweenFields = new HashMap<>();
    // 排序字段
    private Map<String, String> orderFields = new HashMap<>();

    @NotNull(message = "起始页，默认 1 ，传 0 为不分页，不能为NULL")
    private Integer startPage = 1;

    @NotNull(message = "分页时，每页数据量，不能为NULL")
    private Integer pageSize = 10;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getWebsiteCode() {
        return websiteCode;
    }

    public void setWebsiteCode(String websiteCode) {
        this.websiteCode = websiteCode;
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

    @Override
    public String toString() {
        return "GoodsReportParameterDto{" +
                "reportId=" + reportId +
                ", websiteCode='" + websiteCode + '\'' +
                ", groupByFields=" + groupByFields +
                ", whereFields=" + whereFields +
                ", betweenFields=" + betweenFields +
                ", orderFields=" + orderFields +
                ", startPage=" + startPage +
                ", pageSize=" + pageSize +
                '}';
    }

    public String getCacheKey() {
        return MD5CipherUtil.generatePassword(this.toString());
    }

}
