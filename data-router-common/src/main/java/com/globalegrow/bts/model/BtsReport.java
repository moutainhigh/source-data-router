package com.globalegrow.bts.model;

import java.util.Map;

public abstract class BtsReport {

    protected static final String skip = "_skip";

    private String specimen = skip;

    protected Map<String, String> bts;

    public Map<String, String> getBts() {
        return bts;
    }

    public void setBts(Map<String, String> bts) {
        this.bts = bts;
    }

    public String getSpecimen() {
        return specimen;
    }

    public void setSpecimen(String specimen) {
        this.specimen = specimen;
    }
}
