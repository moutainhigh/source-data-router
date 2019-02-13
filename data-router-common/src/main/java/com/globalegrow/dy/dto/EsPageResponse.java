package com.globalegrow.dy.dto;

public abstract class EsPageResponse extends CommonResponse {

    /**
     * 数据总数
     */
    private Long totalCount;

    /**
     * es 分页 scroll id
     */
    private String requestId;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
