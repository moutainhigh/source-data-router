package com.globalegrow.dy.dto;

import java.util.List;
import java.util.Map;

public class UserActionResponseDto {

    private boolean success = true;

    private String message;
    /**
     * 每页数量
     */
    private Integer size = 10;

    private Map<String, List<UserActionData>> data;


    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    /**
     * 分页ID
     */
    private String scrollId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<UserActionData>> getData() {
        return data;
    }

    public void setData(Map<String, List<UserActionData>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserActionResponseDto{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
