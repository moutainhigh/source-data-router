package com.globalegrow.dy.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public abstract class DyRequest{

    /**
     * 调用方
     */
    @NotBlank(message = "调用方不能为空")
    @Length(min = 1, max = 256, message = "调用方字符长度必须在 1-256 之间")
    @ApiModelProperty("接口调用方，必填，不做值校验，算法：algorithm")
    private String target;

}
