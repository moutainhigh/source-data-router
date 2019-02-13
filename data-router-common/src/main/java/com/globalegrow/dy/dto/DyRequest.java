package com.globalegrow.dy.dto;

import org.hibernate.validator.constraints.NotBlank;

public abstract class DyRequest {

    /**
     * 调用方
     */
    @NotBlank(message = "调用方不能为空")
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
