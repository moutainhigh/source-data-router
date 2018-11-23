package com.globalegrow;

import com.globalegrow.util.GsonUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GbBtsInfoUtil {

    public static Map<String, String> gbBtsInfo(Map<String, Object> logMap) {
        String btsString = btsString(String.valueOf(logMap.get("glb_bts")));
        if (btsString.startsWith("{")) {
            Map<String, String> bts = GsonUtil.readValue(btsString, Map.class);
            Map<String, String> fieldInfo = new HashMap<>();
            fieldInfo.put("bts_planid", bts.get("planid"));
            fieldInfo.put("bts_versionid", bts.get("versionid"));
            fieldInfo.put("bts_bucketid", bts.get("bucketid"));
            fieldInfo.put("bts_policy", bts.get("policy"));
            fieldInfo.put("bts_plancode", bts.get("plancode"));
            return fieldInfo;
        }
        if (btsString.startsWith("[")) {
            List<Map<String, String>> list = GsonUtil.readValue(btsString, List.class);
            if (list.size() > 0) {
                Map<String, String> bts = list.get(0);
                Map<String, String> fieldInfo = new HashMap<>();
                fieldInfo.put("bts_planid", bts.get("planid"));
                fieldInfo.put("bts_versionid", bts.get("versionid"));
                fieldInfo.put("bts_bucketid", bts.get("bucketid"));
                fieldInfo.put("bts_policy", bts.get("policy"));
                fieldInfo.put("bts_plancode", bts.get("plancode"));
                return fieldInfo;
            }
        }
        return defaultBtsTestInfo();
    }

    private static String btsString(String btsJsonSource) {
        String btsJson = StringEscapeUtils.unescapeJson(btsJsonSource);
        if (btsJson.startsWith("\"") && btsJson.endsWith("\"")) {
            String s = btsJson.replaceFirst("\"","");
            return s.substring(0, btsJson.lastIndexOf("\"")).replaceAll("\\\\", "");
        }
        return btsJson;
    }

    public static Map<String, String> defaultBtsTestInfo() {
        Map<String, String> fieldInfo = new HashMap<>();
        fieldInfo.put("bts_planid", "_skip");
        fieldInfo.put("bts_versionid", "_skip");
        fieldInfo.put("bts_bucketid", "_skip");
        fieldInfo.put("bts_policy", "_skip");
        fieldInfo.put("bts_plancode", "_skip");
        return fieldInfo;
    }

}
