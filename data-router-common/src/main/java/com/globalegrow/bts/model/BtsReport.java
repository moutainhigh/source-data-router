package com.globalegrow.bts.model;

import java.util.Map;

public abstract class BtsReport {

    protected Map<String, String> bts;

    public Map<String, String> getBts() {
        return bts;
    }

    public void setBts(Map<String, String> bts) {
        this.bts = bts;
    }
}
