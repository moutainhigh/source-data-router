package com.globalegrow.dy.dto;

import java.util.Map;
import java.util.Set;

public class UserActionQueryAllResponse extends EsPageResponse {

    private Map<String,Map<String, Set<UserActionData>>> data;

    public Map<String, Map<String, Set<UserActionData>>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, Set<UserActionData>>> data) {
        this.data = data;
    }
}
