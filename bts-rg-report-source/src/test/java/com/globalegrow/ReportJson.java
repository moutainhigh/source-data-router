package com.globalegrow;

import com.globalegrow.bts.model.BtsReport;
import com.globalegrow.dy.bts.rg.model.BtsRgLoginReport;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ReportJson {

    @Test
    public void loginReportJson() {
        BtsRgLoginReport btsRgLoginReport = new BtsRgLoginReport();
        Map<String, String> bts = new HashMap<>();
        bts.put(BtsReport.btsFields.planid.name(), "");
        bts.put(BtsReport.btsFields.versionid.name(), "");
        bts.put(BtsReport.btsFields.bucketid.name(), "");
        bts.put(BtsReport.btsFields.plancode.name(), "");
        bts.put(BtsReport.btsFields.policy.name(), "");
        btsRgLoginReport.setBts(bts);
        System.out.println(GsonUtil.toJson(btsRgLoginReport));
        Map<String, Object> map = DyBeanUtils.objToMap(btsRgLoginReport);
        map.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
        System.out.println(GsonUtil.toJson(map));
    }

}
