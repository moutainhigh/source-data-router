package com.globalegrow.dy.bts.kafka;

import com.globalegrow.common.CommonBtsLogHandle;
import com.globalegrow.util.GsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GbBtsInfo  extends CommonBtsLogHandle {

    @Override
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
        return super.btsInfo(logMap);
    }
}
