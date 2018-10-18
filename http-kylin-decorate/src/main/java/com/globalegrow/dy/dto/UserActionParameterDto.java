package com.globalegrow.dy.dto;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UserActionParameterDto {

    private String userId;

    private String cookieId;

    private String type;

    @NotNull
    private Long startDate;

    @NotNull
    private Long endDate;

    private List<String> site = new ArrayList<>();

    private List<String> divice = new ArrayList<>();

    /**
     * 分页ID
     */
    private String scrollId;

    /**
     * 每页数量
     */
    private Integer size = 10;

    public String getType() {
        return type;
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

    public void setType(String type) {
        this.type = type;
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

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
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

    @Override
    public String toString() {
        return "UserActionParameterDto{" +
                "userId='" + userId + '\'' +
                ", cookieId='" + cookieId + '\'' +
                ", type='" + type + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", site=" + site +
                ", divice=" + divice +
                '}';
    }
}
