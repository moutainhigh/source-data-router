package com.globalegrow;

import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GbRecommendReportData implements ReportDataLocalBuffer{

    protected static final Logger logger = LoggerFactory.getLogger(GbRecommendReportData.class);


    @Override
    public List<Map> logWriteToRedis(String logString) throws Exception {
        if (logString.contains("/_ubc.gif?")) {
            AppLogReport appLogReport = new AppLogReport();
            appLogReport.setLogSource(logString);
            Map<String, Object> logMap = NginxLogConvertUtil.getNginxLogParameters(logString);
            appLogReport.setLogSourceMap(JacksonUtil.toJSon(logMap));

            if (logMap != null) {

                Map<String, String> bts = this.btsInfo(logMap);
                if (bts != null) {

                    appLogReport.setPlanId(bts.get("planid"));
                    appLogReport.setVersionId(bts.get("versionid"));
                    appLogReport.setBucketId(bts.get("bucketid"));

                    String glbX = String.valueOf(logMap.get("glb_x"));
                    String glbOd = String.valueOf(logMap.get("glb_od"));
                    String glbT = String.valueOf(logMap.get("glb_t"));
                    String glbU = String.valueOf(logMap.get("glb_u"));
                    String glbSkuInfo = String.valueOf(logMap.get("glb_skuinfo"));
                    String glbUbcta = String.valueOf(logMap.get("glb_ubcta"));
                    String glbPm = String.valueOf(logMap.get("glb_pm"));
                    String glbB = String.valueOf(logMap.get("glb_b"));
                    String glbCl = String.valueOf(logMap.get("glb_cl"));

                    appLogReport.setDeviceId(glbOd);

                    boolean isGoodDetail = "c".equals(glbB);

                    if (isGoodDetail) {
                        // 商详页 pv 数
                        boolean isExposureEvent = "ie".equals(glbT);
                        boolean isClickEvent = "ic".endsWith(glbT);
                        boolean isGoodDetailPv = isExposureEvent && StringUtils.isNotEmpty(glbCl) && glbCl.indexOf("/pp_") > 0;
                        boolean pmIsMr = "mr".equals(glbPm);

                        // 曝光数
                        if (isExposureEvent && pmIsMr && StringUtils.isNotEmpty(glbUbcta) && !"null".equals(glbUbcta)) {
                            List<Map<String, String>> ubcs = null;
                            try {
                                ubcs = GsonUtil.readValue(glbUbcta, List.class);
                            } catch (Exception e) {
                                logger.error("子事件解析出错, :{}", logString, e);
                                appLogReport.setIsSuccessHandleEvent(false);
                            }
                            if (ubcs != null && ubcs.size() > 0) {
                                this.logger.debug("曝光商品数");
                                appLogReport.setExpCount(ubcs.size());
                            }
                        }

                        // 点击 & 加购
                        if (isClickEvent) {

                            if (pmIsMr && StringUtils.isNotEmpty(glbUbcta) && !"null".equals(glbUbcta) &&
                                    StringUtils.isNotEmpty(glbSkuInfo) && !"null".equals(glbSkuInfo)) {
                                this.logger.debug("sku 点击事件");
                                appLogReport.setClickCount(1);
                            }
                            if (("mb".equals(glbPm) || "mbt".equals(glbPm)) && ("ADT".equals(glbX) || "BDR".equals(glbX) || "BTS".equals(glbX))
                                    && StringUtils.isNotEmpty(glbSkuInfo) && !"null".equals(glbSkuInfo)) {
                                this.logger.debug("sku 加购数");

                                if (glbSkuInfo.contains("[{")) {
                                    this.logger.debug("buy together");
                                    List<Map<String, Object>> skus = null;
                                    try {
                                        skus = JacksonUtil.readValue(glbSkuInfo, List.class);
                                    } catch (Exception e) {
                                        logger.error("子事件解析出错, :{}", logString, e);
                                        appLogReport.setIsSuccessHandleEvent(false);
                                    }
                                    if (skus != null && skus.size() > 0) {
                                        appLogReport.setAddCartCount(skus.size());
                                    }
                                } else {
                                    this.logger.debug("单个商品加购");
                                    Map<String, Object> sku = null;
                                    try {
                                        sku = JacksonUtil.readValue(glbSkuInfo, Map.class);
                                    } catch (Exception e) {
                                        logger.error("子事件解析出错, :{}", logString, e);
                                        appLogReport.setIsSuccessHandleEvent(false);
                                    }
                                    if (sku != null && sku.size() > 0) {
                                        appLogReport.setAddCartCount(1);
                                    }
                                }

                            }

                        }

                    }
                }




            }else {
                appLogReport.setIsSuccessHandle(false);
            }
            List<Map> maps = new ArrayList<>();
            Map map = DyBeanUtils.objToMap(appLogReport);
            if (logMap == null) {
                map.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
            }else {
                map.put(NginxLogConvertUtil.TIMESTAMP_KEY, (Long) logMap.get(NginxLogConvertUtil.TIMESTAMP_KEY));
            }
            maps.add(map);
            return maps;

        }

        return Collections.emptyList();
    }


    public Map<String, String> btsInfo(Map<String, Object> logMap) {
        String btsInfo = String.valueOf(logMap.get("glb_bts"));
        if (btsInfo.startsWith("[")) {
            List<Map<String, String>> list = GsonUtil.readValue(btsInfo, List.class);
            if (list.size() > 0) {
                Map<String, String> bts = list.get(0);
                Map<String, String> fieldInfo = new HashMap<>();
                fieldInfo.put("planid", bts.get("planid"));
                fieldInfo.put("versionid", bts.get("versionid"));
                fieldInfo.put("bucketid", bts.get("bucketid"));
                fieldInfo.put("policy", bts.get("policy"));
                fieldInfo.put("plancode", bts.get("plancode"));
                fieldInfo.put("mdlc", bts.get("mdlc"));
                return fieldInfo;
            }
        }
        return btsInfo(logMap);
    }

    public Map<String, String> btsInfoDefault(Map<String, Object> logMap) {
        String btsInfo = String.valueOf(logMap.get("glb_bts"));
        this.logger.debug("埋点数据中取到的 bts 实验信息: {}", btsInfo);
        if (btsInfo != null && StringUtils.isNotBlank(btsInfo) && !"null".equals(btsInfo)) {
            this.logger.debug("处理 bts 信息 bts: {}, logInfo: {}", btsInfo, logMap);
            Map<String, String> btsInfoMap = GsonUtil.readValue(btsInfo, Map.class);
            if (btsInfoMap != null && btsInfoMap.size() > 0) {
                return btsInfoMap;
            }
        }
        return null;
    }
}
