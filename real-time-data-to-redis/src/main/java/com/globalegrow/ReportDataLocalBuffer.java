package com.globalegrow;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public interface ReportDataLocalBuffer {

    public default void handleMsgAsync(String logString, LinkedBlockingDeque<Map> linkedBlockingDeque){
        this.logWriteToRedis(logString).stream().forEach(m -> linkedBlockingDeque.offer(m));
    }

    public List<Map> logWriteToRedis(String logString);

}
