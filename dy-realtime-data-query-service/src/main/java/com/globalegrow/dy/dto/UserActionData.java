package com.globalegrow.dy.dto;

import java.util.Objects;

public class UserActionData implements Comparable<UserActionData>{

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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActionData that = (UserActionData) o;
        return workType.equals(that.workType) &&
                time.equals(that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workType, time);
    }

    @Override
    public String toString() {
        return this.workType + "_" + this.time;
    }

    @Override
    public int compareTo(UserActionData o) {
        return o.getTime().compareTo(this.getTime());
    }
}
