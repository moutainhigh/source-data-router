package com.globalegrow;

public class AppLogReport {

    private String planId = "_skip";

    private String versionId = "_skip";

    private String bucketId = "_skip";

    private String deviceId = "_skip";

    private Boolean isSuccessHandle = true;

    private Boolean isSuccessHandleEvent = true;

    private String logSource;

    private String logSourceMap;

    private Integer expCount = 0;

    private Integer clickCount = 0;

    private Integer addCartCount = 0;

    private Integer collectCount = 0;

    private String recommendType = "_skip";

    public String getRecommendType() {
        return recommendType;
    }

    public void setRecommendType(String recommendType) {
        this.recommendType = recommendType;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getIsSuccessHandle() {
        return isSuccessHandle;
    }

    public void setIsSuccessHandle(Boolean successHandle) {
        isSuccessHandle = successHandle;
    }

    public String getLogSource() {
        return logSource;
    }

    public void setLogSource(String logSource) {
        this.logSource = logSource;
    }

    public String getLogSourceMap() {
        return logSourceMap;
    }

    public void setLogSourceMap(String logSourceMap) {
        this.logSourceMap = logSourceMap;
    }

    public Integer getExpCount() {
        return expCount;
    }

    public void setExpCount(Integer expCount) {
        this.expCount = expCount;
    }

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    public Integer getAddCartCount() {
        return addCartCount;
    }

    public void setAddCartCount(Integer addCartCount) {
        this.addCartCount = addCartCount;
    }

    public Boolean getIsSuccessHandleEvent() {
        return isSuccessHandleEvent;
    }

    public void setIsSuccessHandleEvent(Boolean successHandleEvent) {
        isSuccessHandleEvent = successHandleEvent;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }
}
