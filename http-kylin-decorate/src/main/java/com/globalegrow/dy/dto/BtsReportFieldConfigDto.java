package com.globalegrow.dy.dto;

import java.io.Serializable;
import java.util.Objects;

public class BtsReportFieldConfigDto implements Serializable {

    private static final long serialVersionUID = 7421500961280856189L;
    private Long planId;

    private String code;

    private String name;

    public BtsReportFieldConfigDto() {
    }

    public BtsReportFieldConfigDto(Long planId, String code, String name) {
        this.planId = planId;
        this.code = code;
        this.name = name;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BtsReportFieldConfigDto that = (BtsReportFieldConfigDto) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }

    @Override
    public String toString() {
        return "BtsReportFieldConfigDto{" +
                "planId=" + planId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
