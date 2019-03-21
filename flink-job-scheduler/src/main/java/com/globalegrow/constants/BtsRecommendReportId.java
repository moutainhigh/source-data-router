package com.globalegrow.constants;

public enum  BtsRecommendReportId {
    planIdPc("hdfs:///user/hadoop/bts/plan_id_pc_m"),planIdApp("hdfs:///user/hadoop/bts/plan_id_app")
    ;
    private String filePath;

    BtsRecommendReportId(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
