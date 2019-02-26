package com.globalegrow.fixed.queen;

import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.CommonTextUtils;
import lombok.Data;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

@Data
@ToString
public class FBADFeatureMessage extends AbstractFlinkJobQueen {

    private String hdfsPath;

    private static final String HDFS_PATH_KEY = "hdfs_path";

    private String flinkCommandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 /usr/local/services/flink/fb-ad-user-feature-es-0.1.jar --job.hdfs.path ${hdfs_path}";



    public FBADFeatureMessage(String hdfsPath, Long id, Long delayTime) {
        this.hdfsPath = hdfsPath;
        this.id = id;
        this.excuteTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }

    @Override
    String flinkJobCommandLine() {
        return CommonTextUtils.replaceOneParameter(flinkCommandLine, HDFS_PATH_KEY, hdfsPath);
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
