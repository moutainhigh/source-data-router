package com.globalegrow.fixed.scheduler;

import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.JacksonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@Data
@Slf4j
public class ApacheDrillHdfsActiveNameNodeCheck {

    private String apacheDrillAdress = "http://bts-datanode08:8047/storage/";

    private String bigdataDfsDrillConfig = "dfs_bigdata.json";
    private String dyDfsDrillConfig = "dfs.json";

    // 更新方法为 post form
    private String updateBigdataAddress = "dfs_bigdata";
    private String updateDyAddress = "dfs";

    @Autowired
    private RestTemplate restTemplate;

    private String currentDyActiveConfig;

    private String currentBigdataActiveConfig;

    /**
     * 初始化查询 Apache drill 当前配置
     */
    @PostConstruct
    public void init() {
        // 大禹 hdfs 当前配置
        Map<String, Object> result = this.restTemplate.getForObject(this.apacheDrillAdress + this.dyDfsDrillConfig, Map.class);
        log.info("Apache drill 大禹 dfs 配置信息 {}", result);
        this.currentDyActiveConfig = String.valueOf(result.get("connection"));

        // 大数据 hdfs 当前配置
        Map<String, Object> bigdataConfig = this.restTemplate.getForObject(this.apacheDrillAdress + this.bigdataDfsDrillConfig, Map.class);
        log.info("Apache drill 大数据 dfs 配置信息 {}", bigdataConfig);
        this.currentBigdataActiveConfig = String.valueOf(bigdataConfig.get("connection"));

    }

    /**
     * 每分钟检查一次 hdfs 地址是否已切换
     */
    @Scheduled(fixedDelay = 60000)
    public void run() throws Exception {
        String currentDyAddress = HdfsUtil.getDyActiceService();

        if (currentDyAddress.indexOf(this.currentDyActiveConfig) < 0) {
            log.info("大禹 hdfs 主备切换，由 {} 切换为 {}， 更新 Apache drill 配置", this.currentDyActiveConfig, currentDyAddress);
            Map<String, Object> result = this.restTemplate.getForObject(this.apacheDrillAdress + this.dyDfsDrillConfig, Map.class);
            result.put("connection", currentDyAddress);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("name", "dfs");
            map.add("config", JacksonUtil.toJSon(result));

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity( this.apacheDrillAdress + this.updateDyAddress, request , String.class );

            log.info("更新结果 {}", response.getStatusCode());

            this.currentDyActiveConfig = currentDyAddress;
        }

        String currentBigdataAdress = HdfsUtil.getBigDataActiveNamenode();

        if (currentBigdataAdress.indexOf(this.currentBigdataActiveConfig) < 0) {
            log.info("大数据 hdfs 主备切换，由 {} 切换为 {}， 更新 Apache drill 配置", this.currentBigdataActiveConfig, currentBigdataAdress);
            Map<String, Object> bigdataConfig = this.restTemplate.getForObject(this.apacheDrillAdress + this.bigdataDfsDrillConfig, Map.class);
            bigdataConfig.put("connection", currentBigdataAdress);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("name", "dfs_bigdata");
            map.add("config", JacksonUtil.toJSon(bigdataConfig));

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity( this.apacheDrillAdress + this.updateBigdataAddress, request , String.class );

            log.info("更新结果 {}", response.getStatusCode());

            this.currentBigdataActiveConfig = currentBigdataAdress;
        }

    }

}
