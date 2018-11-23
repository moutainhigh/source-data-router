package com.globalegrow;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.JacksonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

@SpringBootApplication(scanBasePackages = {"com.globalegrow"})
@Configuration
@EnableScheduling
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
@RestController
public class ServiceStart {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(ServiceStart.class, args);
    }

    @RequestMapping("count")
    public Object count(String path, String planId) throws Exception {
        Map map = new HashMap();
        Set set = new HashSet();
        int i = 0;
        File fin = new File(path);//读取的文件
        try(LineIterator it = FileUtils.lineIterator(fin, "UTF-8")){
            while (it.hasNext()) {
                String line = it.nextLine();
                if (line.contains("/_app.gif")) {
                    Map<String, Object> map1 = AppLogConvertUtil.getAppLogParameters(line);
                    if (map1 != null) {
                        String eventName = String.valueOf(map1.get("event_name"));
                        String eventJsonValue = String.valueOf(map1.get("event_value"));
                        Map<String, Object> eventValueMap = null;
                        try {
                            eventValueMap = JacksonUtil.readValue(eventJsonValue, Map.class);
                        } catch (Exception e) {
                            logger.error("read value error: {}", eventJsonValue, e);
                        }
                        if (eventValueMap != null) {

                            String deviceId = String.valueOf(map1.get("appsflyer_device_id"));
                            set.add(deviceId);
                            Map<String, String> bts = appLogBtsInfo(eventValueMap);
                            if (bts != null && bts.get("planid").equals(planId)) {
                                if ("af_impression".equals(eventName)) {

                                    String contentIds = String.valueOf(eventValueMap.get("af_content_id"));
                                    if (StringUtils.isNotEmpty(contentIds) && !"null".equals(contentIds)) {
                                        i += contentIds.split(",").length;
                                    }
                                }
                            }

                        }

                    }
                }
                // do something with line
            }
        }
        map.put("exp_count", i);
        map.put("specimen", set.size());
        return map;
    }

    protected static Map<String, String> appLogBtsInfo(Map<String, Object> eventValue) {
        //String eventValue = String.valueOf(logMap.get("event_value"));
        String planId = String.valueOf(eventValue.get("af_plan_id"));
        String versionId = String.valueOf(eventValue.get("af_version_id"));
        String bucketId = String.valueOf(eventValue.get("af_bucket_id"));
        if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketId)
                && !"null".equals(planId) && !"null".equals(versionId) && !"null".equals(bucketId)) {
            Map<String, String> bts = new HashMap<>();
            bts.put("planid", planId);
            bts.put("versionid", versionId);
            bts.put("bucketid", bucketId);
            return bts;
        }
        //logger.warn("bts 实验 id 为空");
        return null;
    }


}
