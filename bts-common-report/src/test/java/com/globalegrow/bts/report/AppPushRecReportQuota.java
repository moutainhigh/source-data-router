package com.globalegrow.bts.report;

public class AppPushRecReportQuota extends BtsAppDopamineReportQuota {

    private int view_push_page = 0;

    private String view_push_uv = skip;

    public int getView_push_page() {
        return view_push_page;
    }

    public void setView_push_page(int view_push_page) {
        this.view_push_page = view_push_page;
    }

    public String getView_push_uv() {
        return view_push_uv;
    }

    public void setView_push_uv(String view_push_uv) {
        this.view_push_uv = view_push_uv;
    }
}
