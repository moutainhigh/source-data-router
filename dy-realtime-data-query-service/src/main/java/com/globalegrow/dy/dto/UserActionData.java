package com.globalegrow.dy.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class UserActionData implements Comparable<UserActionData>{

    private String workType;

    private Long time;

    public UserActionData(String workType, Long time) {
        this.workType = workType;
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
        if (this.getTime().equals(o.getTime()) && !this.getWorkType().equals(o.getWorkType())) {
            return this.getWorkType().compareTo(o.getWorkType());
        }
        return o.getTime().compareTo(this.getTime());
    }
}
