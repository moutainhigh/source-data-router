package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserBaseInfo {

    private String country_code;

    private String device_id;

    private String media_source;

    private String user_id;

    private String language;

    private String platform;

    private Long timestamp;

}
