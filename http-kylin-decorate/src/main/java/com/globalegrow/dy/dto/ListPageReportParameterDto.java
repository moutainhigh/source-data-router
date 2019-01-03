package com.globalegrow.dy.dto;

import com.globalegrow.dy.utils.MD5CipherUtil;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ListPageReportParameterDto implements Serializable {

    private static final long serialVersionUID = -3527989162120185080L;

    @NotNull(message = "报表id不能为NULL")
    private Long reportId;

    @NotEmpty(message = "开始小时不能为NULL")
    private String hourStart;

    @NotEmpty(message = "结束小时不能为NULL")
    private String hourEnd;

    @NotEmpty(message = "网站代码不能为NULL")
    private String websiteCode;

    @NotEmpty(message = "端口不能为NULL")
    private String glbPlf;

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

    public String getHourStart() {
        return hourStart;
    }

    public void setHourStart(String hourStart) {
        this.hourStart = hourStart;
    }

    public String getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(String hourEnd) {
        this.hourEnd = hourEnd;
    }

    public String getWebsiteCode() {
        return websiteCode;
    }

    public void setWebsiteCode(String websiteCode) {
        this.websiteCode = websiteCode;
    }

    public String getGlbPlf() {
        return glbPlf;
    }

    public void setGlbPlf(String glbPlf) {
        this.glbPlf = glbPlf;
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
        return "ListPageReportParameterDto{" +
                "reportId=" + reportId +
                ", hourStart='" + hourStart + '\'' +
                ", hourEnd='" + hourEnd + '\'' +
                ", websiteCode='" + websiteCode + '\'' +
                ", glbPlf='" + glbPlf + '\'' +
                ", startPage=" + startPage +
                ", pageSize=" + pageSize +
                '}';
    }

    public String getCacheKey() {
        return MD5CipherUtil.generatePassword(this.toString());
    }

}
