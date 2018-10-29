package com.globalegrow.dy.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UserActionParameterDto {

    @NotNull
    private String cookieId;

    private List<String> type;

    @NotNull
    private Long startDate;

    @NotNull
    private Long endDate;

    private List<String> site = new ArrayList<>();

    private List<String> divice = new ArrayList<>();


    /**
     * 每页数量
     */
    @Max(1000)
    @NotNull
    private Integer size = 1000;

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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
                ", cookieId='" + cookieId + '\'' +
                ", type='" + type + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", site=" + site +
                ", divice=" + divice +
                '}';
    }
}
