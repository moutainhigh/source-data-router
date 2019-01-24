package com.globalegrow.dy.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UserActionParameterDto extends DyRequest{

    @NotNull(message = "device_id 不能为空")
    @Length(min = 10, max = 256, message = "cookieid 长度为 10-256")
    private String cookieId;

    private List<String> type;

    //@NotNull
    private Long startDate;

    //@NotNull
    private Long endDate;

    @NotBlank(message = "网站标识不能为空")
    private String site;

    //private List<String> platform = new ArrayList<>();

    /**
     * 每页数量
     */
    @Max(1000)
    @Min(0)
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

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "UserActionParameterDto{" +
                ", cookieId='" + cookieId + '\'' +
                ", type='" + type + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
