package com.globalegrow;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class ConfigTest {

    private String apacheDrillAdress = "http://bts-datanode08:8047/storage/";

    private String bigdataDfsDrillConfig = "dfs_bigdata.json";
    private String dyDfsDrillConfig = "dfs.json";

    // 更新方法为 post form
    private String updateBigdataAddress = "dfs_bigdata";
    private String updateDyAddress = "dfs";

    private RestTemplate restTemplate = new RestTemplate();

    private String currentDyActiveConfig;

    private String currentBigdataActiveConfig;

    @Test
    public void test() {
        Map<String, Object> result = this.restTemplate.getForObject(this.apacheDrillAdress + this.dyDfsDrillConfig, Map.class);
        System.out.println(result);

        this.currentDyActiveConfig = String.valueOf(result.get("connection"));

        // 大数据 hdfs 当前配置
        Map<String, Object> bigdataConfig = this.restTemplate.getForObject(this.apacheDrillAdress + this.bigdataDfsDrillConfig, Map.class);
        System.out.println(bigdataConfig);
        Map map = (Map) bigdataConfig.get("config");
        this.currentBigdataActiveConfig = String.valueOf(bigdataConfig.get("connection"));
        System.out.println(currentBigdataActiveConfig);
        map.put("connection", 123);
        System.out.println(bigdataConfig);
    }

}
