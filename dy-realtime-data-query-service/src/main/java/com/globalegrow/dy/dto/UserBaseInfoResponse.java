package com.globalegrow.dy.dto;

import java.util.List;

public class UserBaseInfoResponse extends EsPageResponse {

    private List<UserBaseInfo> data;


    public List<UserBaseInfo> getData() {
        return data;
    }

    public void setData(List<UserBaseInfo> data) {
        this.data = data;
    }
}
