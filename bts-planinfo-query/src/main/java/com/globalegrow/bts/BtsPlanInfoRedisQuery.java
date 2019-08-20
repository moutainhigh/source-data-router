package com.globalegrow.bts;

import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BtsPlanInfoRedisQuery
        implements BtsPlanInfoQuery
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public enum zafulBts {
        versionid, bucketid, planid; }

    public enum bts {
        bucket_id, plan_id, version_id;
    }










    public Map<String, String> queryBtsInfo(String cookie, String productCode, String planCode) {
        String redisKey = "BTS_CP_" + productCode + "_" + cookie + "_" + planCode;


        String planInfo = SpringRedisUtil.getStringValue(redisKey);



        if (StringUtils.isNotEmpty(planInfo)) {
            try {
                Map<String, Object> infoMap = (Map)JacksonUtil.readValue(planInfo, Map.class);
                Map<String, String> map = new HashMap<String, String>();
                map.put("bucket_id", String.valueOf(infoMap.get("bucketId")));



                map.put("plan_id", String.valueOf(infoMap.get("planId")));
                map.put("version_id", String.valueOf(infoMap.get("versionId")));
                return map;
            }
            catch (Exception e) {
                this.logger.error("����������������", e);
            }
        }
        this.logger.info("redis key: {}, planInfo: {}", redisKey, planInfo);
        return null;
    }


    public Map<String, String> queryBtsInfo(Map<String, Object> dataMap) {
        Map<String, String> bts = null;
        Object ubcta = dataMap.get("glb_ubcta");
        if (ubcta != null) {
            this.logger.debug("������������ bts ����");
            if (ubcta instanceof Map) {
                bts = buildBtsMap((Map)ubcta);
            } else if (ubcta instanceof List) {
                List<Map<String, String>> mapList = (List)ubcta;
                if (mapList.size() > 0) {
                    bts = buildBtsMap((Map)mapList.get(0));
                }
            } else if (ubcta instanceof String) {
                String ubctaStr = String.valueOf(ubcta);
                if (StringUtils.isNotEmpty(ubctaStr)) {
                    bts = getBtsInfoFromUbcta(ubctaStr);
                }
            }
        }


        return bts;
    }

    private Map<String, String> getBtsInfoFromUbcta(String ubcta) {
        this.logger.debug("uncta ����: {}", ubcta);
        if (ubcta.startsWith("[")) {
            List<Map<String, String>> mapList = (List)GsonUtil.readValue(ubcta, List.class);
            if (mapList.size() > 0) {
                return buildBtsMap((Map)mapList.get(0));
            }
        }
        if (ubcta.startsWith("{")) {
            return buildBtsMap((Map)GsonUtil.readValue(ubcta, Map.class));
        }
        return null;
    }

    private Map<String, String> buildBtsMap(Map<String, String> ubMap) {
        this.logger.debug("�������� ubcta ����: {}", ubMap);
        String planid = (String)ubMap.get(zafulBts.planid.name());
        String versionId = (String)ubMap.get(zafulBts.versionid.name());
        String bucketid = (String)ubMap.get(zafulBts.bucketid.name());
        if (StringUtils.isNotEmpty(planid) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketid)) {
            Map<String, String> btsMap = new HashMap<String, String>();
            btsMap.put(bts.plan_id.name(), planid);
            btsMap.put(bts.version_id.name(), versionId);
            btsMap.put(bts.bucket_id.name(), bucketid);
            return btsMap;
        }
        return null;
    }
}
