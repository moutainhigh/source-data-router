package com.globalegrow.dy.bts.kafka;

import com.globalegrow.common.hbase.CommonHbaseMapper;
import com.globalegrow.dy.bts.model.SkuCartInfo;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存加购信息
 */
@Component
public class ZafulRecommendCartHandle {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Configuration configuration;

    private Connection connection;

    Table table;

    @PostConstruct
    public void init() throws IOException {
        connection = ConnectionFactory.createConnection(this.configuration);
        table = connection.getTable(TableName.valueOf("dy_cookie_bts_info_rel"));
    }

    @Autowired
    private CommonHbaseMapper hbaseMapper;

    @Value("${app.hbase.zfapp-userid-table}")
    private String hbaseTableName;

    @Value("${app.hbase.zfapp-user-clumn-family}")
    private String columnFamily;

    @KafkaListener(topics = {"glbg-analitic-json-app"}, groupId = "app_dopamine_order")
    public void cacheCartInfo(String log) {
        // zaful 加购事件
        if (log.contains("af_add_to_bag") && log.toLowerCase().contains("zaful")) {
            Map<String, Object> logMap = GsonUtil.readValue(log, Map.class);
            String eventName = String.valueOf(logMap.get("event_name"));
            if ("af_add_to_bag".equals(eventName)) {
                String eventValue = String.valueOf(logMap.get("event_value"));
                Map<String, Object> eventValueMap = GsonUtil.readValue(eventValue, Map.class);
                String deviceId = String.valueOf(logMap.get("appsflyer_device_id"));
                String userId = String.valueOf(logMap.get("customer_user_id"));
                String sku = String.valueOf(eventValueMap.get("af_content_id"));
                if (StringUtils.isEmpty(userId) || "null".equals(userId)) {
                    userId = this.queryUserId(deviceId);
                }

                if (userId != null) {
                    Map<String, String> bts = appLogBtsInfo(eventValueMap);
                    Boolean isRecommend;
                    String recommendType = String.valueOf(eventValueMap.get("af_inner_mediasource"));
                    if (StringUtils.isNotEmpty(recommendType) && !"null".equals(recommendType) && recommendType.contains("recommend") && bts != null) {
                        isRecommend = true;
                    } else {
                        isRecommend = false;
                    }

                    if (bts == null) {
                        this.logger.debug("从 hbase 中查询 bts 实验信息");

                        List<Map<String, String>> btsList = this.getBtsInfoFromHbase(deviceId);
                        // this.logger.info("从 hbase 查询 bts 信息:{}", btsList);
                        if (btsList != null && btsList.size() > 0) {
                            SkuCartInfo skuCartInfo = new SkuCartInfo();
                            skuCartInfo.setSku(sku);
                            skuCartInfo.setBts(btsList.get(0));
                            skuCartInfo.setUserId(userId);
                            skuCartInfo.setDeviceId(deviceId);
                            skuCartInfo.setRecommend(isRecommend);
                            String redisKey = "dy_zaful_app_dopamine_" + userId + "_" + sku;
                            SpringRedisUtil.put(redisKey, GsonUtil.toJson(skuCartInfo), 604800);
                        }

                    } else if (bts != null) {
                        SkuCartInfo skuCartInfo = new SkuCartInfo();
                        skuCartInfo.setSku(sku);
                        skuCartInfo.setBts(bts);
                        skuCartInfo.setUserId(userId);
                        skuCartInfo.setDeviceId(deviceId);
                        skuCartInfo.setRecommend(isRecommend);
                        String redisKey = "dy_zaful_app_dopamine_" + userId + "_" + sku;
                        SpringRedisUtil.put(redisKey, GsonUtil.toJson(skuCartInfo), 604800);
                    }


                }

            }

        }

    }

    private List<Map<String, String>> getBtsInfoFromHbase(String deviceId) {
        try {
            Scan s = new Scan();
            s.setFilter(new PrefixFilter(deviceId.getBytes()));
            ResultScanner rs = table.getScanner(s);
            if (rs != null) {
                List<Map<String, String>> list = new ArrayList<>();
                for (Result result : rs) {
                    String value = Bytes.toString(result.getValue(Bytes.toBytes("bts_info"), Bytes.toBytes("info")));
                    if (StringUtils.isNotEmpty(value) && !"null".equals(value)) {
                        list.add(GsonUtil.readValue(value, Map.class));
                    }

                }
                return list;
            }
        } catch (IOException e) {
            this.logger.error("从 hbase 查询 bts 信息失败", e);
        }
        return null;
    }

    protected Map<String, String> appLogBtsInfo(Map<String, Object> eventValue) {
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
        this.logger.warn("bts 实验 id 为空");
        return null;
    }

    private String queryUserId(String deviceId) {
        return getUserIdByDeviceId(deviceId, this.logger, this.hbaseMapper, this.hbaseTableName, this.columnFamily);
    }

    static String getUserIdByDeviceId(String deviceId, Logger logger, CommonHbaseMapper hbaseMapper, String hbaseTableName, String columnFamily) {
        logger.debug("查询 userid");
        Object obj = hbaseMapper.selectRowKeyFamilyColumn(hbaseTableName, deviceId,
                "userid", columnFamily);
        if (obj != null) {
            return String.valueOf(obj);
        }
        logger.info("加购事件未找到 userid, cookie: {}", deviceId);
        return null;
    }

}
