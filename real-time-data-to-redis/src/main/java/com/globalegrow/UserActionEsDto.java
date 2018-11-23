package com.globalegrow;

/**
 * elasticsearch 用户行为数据结构
 */
public class UserActionEsDto {

    private String site;

    private String device_id;

    private String user_id;

    private String event_name;

    private String event_value;

    private String platform;

    private Long timestamp;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_value() {
        return event_value;
    }

    public void setEvent_value(String event_value) {
        this.event_value = event_value;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserActionEsDto{" +
                "site='" + site + '\'' +
                ", device_id='" + device_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", event_name='" + event_name + '\'' +
                ", event_value='" + event_value + '\'' +
                ", platform='" + platform + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
