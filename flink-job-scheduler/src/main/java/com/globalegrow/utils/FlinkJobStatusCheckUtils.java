package com.globalegrow.utils;

import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class FlinkJobStatusCheckUtils {

    public static final String NOT_FOUND = "NOT_FOUND";

    private static String hdfsPath = "hdfs://glbgnameservice/user/hadoop/flink/completed-jobs/";

    public static String getJobStatusByJobId(String jobId) {
        if (HdfsUtil.dyFileExist(hdfsPath + jobId)) {
            try {
                String json = HdfsUtil.getDyFileContentString(hdfsPath + jobId);

                Map<String, Object> map = JacksonUtil.readValue(json, Map.class);
                List<Map<String,Object>> list = (List) map.get("archive");

                Map<String,Object> result = list.stream().filter(map1 -> "/jobs/overview".equals(map1.get("path"))).findFirst().get();

                log.info("job 运行结果 {}",result);
                Map<String, Object> jobson = JacksonUtil.readValue((String) result.get("json"), Map.class);

                List<Map<String, Object>> jobs = (List<Map<String, Object>>) jobson.get("jobs");
                return String.valueOf(jobs.get(0).get("state"));
            } catch (Exception e) {
                log.error("查询 job 状态出错 {}", e, jobId);
            }
        }else {
            return NOT_FOUND;
        }
        return "FINISHED";
    }

}
