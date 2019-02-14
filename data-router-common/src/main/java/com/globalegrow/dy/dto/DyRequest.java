package com.globalegrow.dy.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public abstract class DyRequest extends CommonToStringModel{

    /**
     * 调用方
     */
    @NotBlank(message = "调用方不能为空")
    @Length(min = 1, max = 256, message = "调用方字符长度必须在 1-256 之间")
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
