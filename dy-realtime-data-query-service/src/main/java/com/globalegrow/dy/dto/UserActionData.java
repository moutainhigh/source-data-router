package com.globalegrow.dy.dto;

public class UserActionData {

    private String workType;

    private Long time;

    public UserActionData(String workType, Long time) {
        this.workType = workType;
        this.time = time;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
