package com.globalegrow.dy.dto;

import java.util.List;
import java.util.Map;

public class UserActionQueryAllResponse extends EsPageResponse {

    private Map<String,Map<String, List<UserActionData>>> data;

    public Map<String, Map<String, List<UserActionData>>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, List<UserActionData>>> data) {
        this.data = data;
    }
}
