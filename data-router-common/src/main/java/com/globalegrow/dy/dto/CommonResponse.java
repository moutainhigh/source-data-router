package com.globalegrow.dy.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class CommonResponse{

    @ApiModelProperty("请求是否成功")
    private boolean success = true;
    @ApiModelProperty("请求消息内容")
    private String message;

}