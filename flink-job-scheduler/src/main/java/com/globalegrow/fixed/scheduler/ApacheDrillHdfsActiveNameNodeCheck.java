package com.globalegrow.fixed.scheduler;

import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.JacksonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.drill.storage}")
    private String apacheDrillAdress = "http://bts-datanode05:8047/storage/";

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
     * SELECT hostname FROM sys.drillbits WHERE `current` = true;
     */
    @PostConstruct
    public void init() {
        // 大禹 hdfs 当前配置
        Map<String, Object> result = this.restTemplate.getForObject(this.apacheDrillAdress + this.dyDfsDrillConfig, Map.class);
        log.info("Apache drill 大禹 dfs 配置信息 {}", result);
        Map dymap = (Map) result.get("config");
        this.currentDyActiveConfig = String.valueOf(dymap.get("connection"));


        // 大数据 hdfs 当前配置
        Map<String, Object> bigdataConfig = this.restTemplate.getForObject(this.apacheDrillAdress + this.bigdataDfsDrillConfig, Map.class);
        log.info("Apache drill 大数据 dfs 配置信息 {}", bigdataConfig);
        Map map = (Map) bigdataConfig.get("config");
        this.currentBigdataActiveConfig = String.valueOf(map.get("connection"));

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
            Map dymap = (Map) result.get("config");
            dymap.put("connection", currentDyAddress);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("name", String.valueOf(result.get("name")));
            map.add("config", JacksonUtil.toJSon(dymap));

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity( this.apacheDrillAdress + this.updateDyAddress, request , String.class );

            log.info("更新结果 {}", response.getStatusCode());

            this.currentDyActiveConfig = currentDyAddress;
        }

        String currentBigdataAdress = HdfsUtil.getBigDataActiveNamenode();

        if (currentBigdataAdress.indexOf(this.currentBigdataActiveConfig) < 0) {
            log.info("大数据 hdfs 主备切换，由 {} 切换为 {}， 更新 Apache drill 配置", this.currentBigdataActiveConfig, currentBigdataAdress);
            Map<String, Object> bigdataConfig = this.restTemplate.getForObject(this.apacheDrillAdress + this.bigdataDfsDrillConfig, Map.class);
            Map mapConfig = (Map) bigdataConfig.get("config");
            mapConfig.put("connection", currentBigdataAdress);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("name", String.valueOf(bigdataConfig.get("name")));
            map.add("config", JacksonUtil.toJSon(mapConfig));

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity( this.apacheDrillAdress + this.updateBigdataAddress, request , String.class );

            log.info("更新结果 {}", response.getStatusCode());

            this.currentBigdataActiveConfig = currentBigdataAdress;
        }

    }

}
