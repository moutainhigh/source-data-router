package com.globalegrow.fixed.queen;

import com.globalegrow.fixed.enums.NoticeType;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
@RequiredArgsConstructor
public class FlinkBashJob {

    @NonNull
    private String jobName;

    @NonNull
    private String flinkCommandLine;
    private String date;

    public FlinkBashJob(@NonNull String jobName, @NonNull String flinkCommandLine, String date) {
        this.jobName = jobName;
        this.flinkCommandLine = flinkCommandLine;
        this.date = date;
    }

    private int runTimes = 1;

    private NoticeType noticeType;

    private Map<String, String> noticeInfo = new HashMap<>();

}
