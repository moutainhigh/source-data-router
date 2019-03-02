package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class CommonResponse{

    private boolean success = true;

    private String message;

}
