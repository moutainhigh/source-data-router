package com.globalegrow.dy.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class CommonPatternResponse<T> extends CommonResponse {

    @ApiModelProperty("响应数据")
    private T data;

}
