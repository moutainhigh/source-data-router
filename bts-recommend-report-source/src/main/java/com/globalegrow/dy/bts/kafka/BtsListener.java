package com.globalegrow.dy.bts.kafka;

import com.globalegrow.bts.enums.BtsFields;
import com.globalegrow.common.CommonLogHandle;
import com.globalegrow.util.GsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class BtsListener extends CommonLogHandle {

    public Map<String, String> btsInfo(Map<String, Object> logMap) {
        String btsInfo = String.valueOf(logMap.get("glb_bts"));
        this.logger.debug("埋点数据中取到的 bts 实验信息: {}", btsInfo);
        if (btsInfo != null && StringUtils.isNotBlank(btsInfo) && !"null".equals(btsInfo)) {
            this.logger.debug("处理 bts 信息 bts: {}, logInfo: {}", btsInfo, logMap);
            Map<String, String> btsInfoMap = GsonUtil.readValue(btsInfo, Map.class);
            if (btsInfoMap != null) {
              /*  Map<String, String> btsReportMap = new HashMap<>();
                btsReportMap.put(BtsFields.plan_id.name(), btsInfoMap.get("planid"));
                btsReportMap.put(BtsFields.version_id.name(), btsInfoMap.get("versionid"));
                btsReportMap.put(BtsFields.bucket_id.name(), btsInfoMap.get("bucketid"));
                btsReportMap.put(BtsFields.policy.name(), btsInfoMap.get("policy"));*/
                return btsInfoMap;
            }
        }
        return null;
    }

}
