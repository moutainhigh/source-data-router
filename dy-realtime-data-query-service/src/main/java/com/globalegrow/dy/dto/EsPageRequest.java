package com.globalegrow.dy.dto;

/**
 * es 分页对象
 */
public abstract class EsPageRequest extends DyRequest{

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
}
