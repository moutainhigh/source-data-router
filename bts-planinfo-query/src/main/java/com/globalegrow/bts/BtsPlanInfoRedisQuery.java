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
public class BtsPlanInfoRedisQuery implements BtsPlanInfoQuery {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public  enum zafulBts{
        versionid, bucketid, planid
    }
    public enum bts {
        bucket_id, plan_id, version_id
    }

    /**
     * 根据推荐位和 cookie 查询
     *  "{\"bucketid\":6,\"runplaninfo\":{\"2020201\":{\"planid\":9,\"versionflag\":\"0\",\"versionid\":23}},\"updatetime\":1532590224539}"
     * @param cookie
     * @param productCode
     * @param planCode
     * @return
     */
    @Override
    public Map<String, String> queryBtsInfo(String cookie, String productCode, String planCode) {
        String redisKey = "BTS_CP_" + productCode + "_" + cookie + "_" + planCode;
        // String redisKey = "BTS_R_" + productCode + "_" + cookie;

        String planInfo = SpringRedisUtil.getStringValue(redisKey);



        if (StringUtils.isNotEmpty(planInfo)) {
            try {
                Map<String, Object> infoMap = JacksonUtil.readValue(planInfo, Map.class);
                Map<String, String> map = new HashMap<>();
                map.put("bucket_id", String.valueOf(infoMap.get("bucketId")));
                //Map<String, Object> runplaninfo = (Map<String, Object>) infoMap.get("runplaninfo");
                //Map<String, Object> typeInfo = (Map<String, Object>) runplaninfo.get(planCode);
                //if (typeInfo != null && typeInfo.size() > 0) {
                    map.put("plan_id", String.valueOf(infoMap.get("planId")));
                    map.put("version_id", String.valueOf(infoMap.get("versionId")));
                    return map;
                //}
            } catch (Exception e) {
                logger.error("读取实验信息出错", e);
            }
        }
        logger.info("redis key: {}, planInfo: {}", redisKey, planInfo);
        return null;
    }

    @Override
    public Map<String, String> queryBtsInfo(Map<String, Object> dataMap) {
        Map<String, String> bts = null;
        Object ubcta = dataMap.get("glb_ubcta");
        if (ubcta != null) {
            this.logger.debug("从埋点中获取 bts 信息");
            if (ubcta instanceof Map) {
                bts = this.buildBtsMap((Map<String, String>) ubcta);
            } else if (ubcta instanceof List) {
                List<Map<String, String>> mapList = (List<Map<String, String>>) ubcta;
                if (mapList.size() > 0) {
                    bts = this.buildBtsMap(mapList.get(0));
                }
            } else if (ubcta instanceof String) {
                String ubctaStr = String.valueOf(ubcta);
                if (StringUtils.isNotEmpty(ubctaStr)) {
                    bts = this.getBtsInfoFromUbcta(ubctaStr);
                }
            }

        }

        return bts;
    }

    private Map<String, String> getBtsInfoFromUbcta(String ubcta) {
        this.logger.debug("uncta 信息: {}", ubcta);
        if (ubcta.startsWith("[")) {
            List<Map<String, String>> mapList = GsonUtil.readValue(ubcta, List.class);
            if (mapList.size() > 0) {
                return this.buildBtsMap(mapList.get(0));
            }
        }
        if (ubcta.startsWith("{")) {
            return this.buildBtsMap(GsonUtil.readValue(ubcta, Map.class));
        }
        return null;
    }

    private Map<String, String> buildBtsMap(Map<String, String> ubMap) {
        this.logger.debug("埋点中的 ubcta 信息: {}", ubMap);
        String planid = ubMap.get(zafulBts.planid.name());
        String versionId = ubMap.get(zafulBts.versionid.name());
        String bucketid = ubMap.get(zafulBts.bucketid.name());
        if (StringUtils.isNotEmpty(planid) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketid)) {
            Map<String, String> btsMap = new HashMap<>();
            btsMap.put(bts.plan_id.name(), planid);
            btsMap.put(bts.version_id.name(), versionId);
            btsMap.put(bts.bucket_id.name(), bucketid);
            return btsMap;
        }
        return null;
    }
}
