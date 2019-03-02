package com.globalegrow.fixed.queen;

import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class DyHdfsCheckExistsJobMessage extends AbstractFlinkJobQueen{

    protected String hdfsPath;

    public DyHdfsCheckExistsJobMessage(String hdfsPath, Long id, Long delayTime,String flinkJobCommandLine) {
        this.hdfsPath = hdfsPath;
        this.id = id;
        this.executeTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
        this.flinkJobCommandLine = flinkJobCommandLine;
    }

    /**
     * 是否满足可过滤条件，如 hdfs 文件是否存在等等
     *
     * @return
     */
    @Override
    public boolean canRun() {
        return HdfsUtil.dyFileExist(this.hdfsPath);
    }
}
