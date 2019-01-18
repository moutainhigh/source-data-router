package com.globalegrow.dy.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户基本信息参数
 */
public class UserBaseInfoRequest extends EsPageRequest {

    private String cookieId;

    private List<String> platform = new ArrayList<>();
    @Max(1000)
    @Min(0)
    @NotNull
    private Integer size = 100;

    @NotBlank(message = "网站源不能为空")
    private String site;

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public List<String> getPlatform() {
        return platform;
    }

    public void setPlatform(List<String> platform) {
        this.platform = platform;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
