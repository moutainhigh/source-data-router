package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class CommonPatternResponse<T> extends CommonResponse {

    private T data;

}
