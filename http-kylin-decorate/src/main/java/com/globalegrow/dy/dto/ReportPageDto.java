package com.globalegrow.dy.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportPageDto implements Serializable {

    private static final long serialVersionUID = 8864000217789639409L;

    private long totalCount = 0;

    private long totalPage = 0;

    private int currentPage = 0;

    private int pageSize = 10;

    private List<Object> data = new ArrayList<>();

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "ReportPageDto{" +
                "data=" + data +
                ", totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}
