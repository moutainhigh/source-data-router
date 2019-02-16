package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserBaseInfoResponse extends EsPageResponse {

    private List<UserBaseInfo> data;

}
