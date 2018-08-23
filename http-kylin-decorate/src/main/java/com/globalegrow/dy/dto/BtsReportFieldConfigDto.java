package com.globalegrow.dy.dto;

public class BtsReportFieldConfigDto {

    private Long planId;

    private String code;

    private String name;

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
    public String toString() {
        return "BtsReportFieldConfigDto{" +
                "planId=" + planId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
