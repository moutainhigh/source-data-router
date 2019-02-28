package com.globalegrow.fixed.queen;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
public class FlinkBuryLogDataJob {

    @NonNull
    private String jobName;

    @NonNull
    private String flinkCommandLine;

}
