package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

/**
 * elasticsearch 用户行为数据结构
 */
@Data
@ToString
public class UserActionEsDto {

    private String site;

    private String device_id;

    private String user_id;

    private String event_name;

    private String event_value;

    private String platform;

    private Long timestamp;

}
