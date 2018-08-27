package com.globalegrow.common;

import com.globalegrow.util.GsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PC 报表通用对象
 */
public class CommonLogModel {

    private Map<String, Object> sourceLog;

    public CommonLogModel(Map<String, Object> sourceLog) {
        this.sourceLog = sourceLog;
    }

    public String getGlbS() {
        return this.getLogValue("glb_s");
    }

    public String getGlbPlf() {
        return this.getLogValue("glb_plf");
    }

    public String getGlbFilter() {
        return this.getLogValue("glb_filter");
    }

    public String getGlbX() {
        return this.getLogValue("glb_x");
    }

    public String getGlbSkuInfo() {
        return this.getLogValue("glb_skuinfo");
    }

    public String getGlbUbcta() {
        return this.getLogValue("glb_ubcta");
    }

    public String getGlbOd() {
        return this.getLogValue("glb_od");
    }

    public String getGlbT() {
        return this.getLogValue("glb_t");
    }

    public String getGlbU() {
        return this.getLogValue("glb_u");
    }

    public String getGlbCl() {
        return this.getLogValue("glb_cl");
    }

    public Map<String, Object> getSkuMap() {
        if (StringUtils.isNotEmpty(this.getGlbSkuInfo())) {
            return GsonUtil.readValue(this.getGlbSkuInfo(), Map.class);
        }
        return Collections.EMPTY_MAP;
    }

    public Map<String, String> getUbcMap() {
        if (StringUtils.isNotEmpty(this.getGlbUbcta())) {
            return GsonUtil.readValue(this.getGlbUbcta(), Map.class);
        }
        return Collections.EMPTY_MAP;
    }

    public List<Map<String, String>> getUbcList() {
        if (StringUtils.isNotEmpty(this.getGlbUbcta()) && this.getGlbUbcta().contains("[{")) {
            return GsonUtil.readValue(this.getGlbUbcta(), List.class);
        }
        return Collections.EMPTY_LIST;
    }

    private String getLogValue(String key) {
        if (this.sourceLog.get(key) != null) {
            String value = String.valueOf(this.sourceLog.get(key));
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return "";
    }
}
