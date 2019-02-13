package com.globalegrow.dy.dto;

import java.util.List;

public class SearchWordSkusResponse extends CommonResponse {

    private List<String> data;


    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
