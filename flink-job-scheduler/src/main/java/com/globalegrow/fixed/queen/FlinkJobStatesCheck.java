package com.globalegrow.fixed.queen;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
@ToString
@Deprecated
public class FlinkJobStatesCheck implements Delayed {

    protected Long id;
    protected long executeTime;//执行时间

    private String jobId;

    private Boolean successNotice;

    private String emails;

    public FlinkJobStatesCheck(Long id, String jobId, Long delayTime) {
        this.id = id;
        this.jobId = jobId;
        this.executeTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.executeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        FlinkJobStatesCheck message = (FlinkJobStatesCheck) o;
        return message.getId().compareTo(this.getId());
    }

}
