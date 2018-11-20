package com.globalegrow.report;

import com.globalegrow.common.hbase.CommonHbaseMapper;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.MD5CipherUtil;
import com.globalegrow.util.SpringRedisUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 埋点数据 redis 缓存
 */
@Component
public class LogDataRedisCache implements LogDataCache {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JestClient jestClient;

    public static final String APP_REPORT_END_FLAG = "_APP";

    @Autowired
    private CommonHbaseMapper commonHbaseMapper;

    /**
     * 将埋点数据进行缓存主要为加购数据缓存,目前只支持加购信息缓存
     * glb_skuinfo: {"sku":"299715908","price":"20.99","pam":"1","pc":"139","zt":0}
     *
     * @param reportName
     * @param jsonMap
     */
    @Override
    public void cacheData(String reportName, Map<String, Object> jsonMap, Long expireSeconds) {
        try {
            if (reportName.endsWith(APP_REPORT_END_FLAG)) {

                this.logger.debug("app 端的用户加购数据缓存");

                String userId = String.valueOf(jsonMap.get("customer_user_id"));
                Map<String, Object> eventValue = (Map<String, Object>) jsonMap.get("event_value");

                if (eventValue != null) {
                    String sku = String.valueOf(eventValue.get("af_content_id"));
                    if (StringUtils.isNotEmpty(sku) && !"null".equals(sku)) {

                        if (StringUtils.isEmpty(userId) || "0".equals(userId)) {

                            String id = String.valueOf(jsonMap.get("appsflyer_device_id")) + "_" + String.valueOf(jsonMap.get("app_name")) + "_" + String.valueOf(jsonMap.get("platform"));
                            this.logger.debug("根据 cookie 站点等信息查询用户 id 信息: {}", id);
                            Get get = new Get.Builder("cookie-userid-rel", MD5CipherUtil.generatePassword(id)).type("userid").build();
                            userId = getUserIdFromEs(userId, id, get);


                        }

                        this.addCartLogCacheToRedis(reportName, jsonMap, userId, sku);

                    }

                }

            } else {
                String userId = String.valueOf(jsonMap.get("glb_u"));
                Map<String, Object> skuInfo = (Map<String, Object>) jsonMap.get("glb_skuinfo");
                if (skuInfo != null) {
                    String sku = String.valueOf(skuInfo.get("sku"));
                    if (StringUtils.isNotEmpty(sku) && !"null".equals(sku)) {
                        if (jsonMap.get("glb_u") == null || "0".equals(jsonMap.get("glb_u"))) {

                            String id = String.valueOf(jsonMap.get("glb_od")) + "_" + String.valueOf(jsonMap.get("glb_d")) + "_" + String.valueOf(jsonMap.get("glb_dc"));
                            this.logger.debug("根据 cookie 站点等信息查询用户 id 信息: {}", id);
                            Get get = new Get.Builder("cookie-userid-rel", MD5CipherUtil.generatePassword(id)).type("userid").build();
                            userId = getUserIdFromEs(userId, id, get);
                        }

                        this.addCartLogCacheToRedis(reportName, jsonMap, userId, sku);

                    }

                }
            }
        } catch (Exception e) {
            logger.error("加购数据缓存出错", e);
        }

    }

    private void addCartLogCacheToRedis(String reportName, Map<String, Object> jsonMap, String userId, String sku) {
        if (StringUtils.isNotEmpty(userId) && !"null".equals(userId)) {
            this.logger.debug("用户加购埋点数据缓存至 redis，以计算用户订单相关维度数据。");
            String redisKey = reportName + "_" + userId + "_" + sku;
            try {
                SpringRedisUtil.put(redisKey, JacksonUtil.toJSon(jsonMap), 1209600);
            } catch (Exception e) {
                this.logger.error("埋点数据缓存失败key : {},value: {}", redisKey, jsonMap);
            }
        }
    }

    private String getUserIdFromEs(String userId, String id, Get get) {
        try {
            DocumentResult result = this.jestClient.execute(get);
            if (result != null) {
                Map<String, String> cookieUserIdMap = result.getSourceAsObject(Map.class);
                this.logger.debug("根据 cookie 站点等信息查询用户 id 结果:{} ,{}", id, cookieUserIdMap);
                if (cookieUserIdMap != null) {
                    userId = cookieUserIdMap.get("userid");
                }
            }
        } catch (Exception e) {
            logger.error("查询用户id error", e);
        }
        return userId;
    }
}
