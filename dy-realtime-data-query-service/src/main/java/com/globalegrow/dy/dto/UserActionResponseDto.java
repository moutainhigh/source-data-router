package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@Data
@ToString
public class UserActionResponseDto {

    private boolean success = true;

    private String message;
    /**
     * 每页数量
     */
    private Integer size = 10;

    private Map<String, Set<UserActionData>> data;

    /**
     * 分页ID
     */
    private String scrollId;

}
