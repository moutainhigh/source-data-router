package com.globalegrow.dy.dto;

public abstract class CommonPatternResponse<T> extends CommonResponse {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
