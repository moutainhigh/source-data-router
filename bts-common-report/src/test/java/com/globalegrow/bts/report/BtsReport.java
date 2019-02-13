package com.globalegrow.bts.report;

import java.util.Map;

public abstract class BtsReport {

    public enum btsFields{
        planid,versionid,bucketid,plancode,policy
    }

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
