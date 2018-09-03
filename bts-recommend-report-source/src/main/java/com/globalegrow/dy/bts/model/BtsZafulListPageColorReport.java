package com.globalegrow.dy.bts.model;

import com.globalegrow.bts.model.BtsReport;

public class BtsZafulListPageColorReport extends BtsReport {
    // 页面总 UV
    private String pageUv = skip;
    // 商品点击
    private String goodClickUv = skip;
    // 商品收藏
    private String goodCollectUv = skip;
    // 页面停留时间
    private Integer pageStayMillSecs = 0;

    public String getPageUv() {
        return pageUv;
    }

    public void setPageUv(String pageUv) {
        this.pageUv = pageUv;
    }

    public String getGoodClickUv() {
        return goodClickUv;
    }

    public void setGoodClickUv(String goodClickUv) {
        this.goodClickUv = goodClickUv;
    }

    public String getGoodCollectUv() {
        return goodCollectUv;
    }

    public void setGoodCollectUv(String goodCollectUv) {
        this.goodCollectUv = goodCollectUv;
    }

    public Integer getPageStayMillSecs() {
        return pageStayMillSecs;
    }

    public void setPageStayMillSecs(Integer pageStayMillSecs) {
        this.pageStayMillSecs = pageStayMillSecs;
    }
}
