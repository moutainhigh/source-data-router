package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@Data
@ToString
public class UserActionQueryAllResponse extends EsPageResponse {

    private Map<String,Map<String, Set<UserActionData>>> data;

}
