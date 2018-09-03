package com.globalegrow.common;

import com.globalegrow.util.GsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class CommonModelBtsLogHandle extends LogHandleWithCommonModel{

    public Map<String, String> btsInfo(CommonLogModel commonLogModel) {
        Map<String, Object> logMap = commonLogModel.getSourceLog();
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
