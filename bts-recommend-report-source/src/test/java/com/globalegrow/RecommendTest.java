package com.globalegrow;

import com.globalegrow.bts.RecommendTypeUtil;
import com.globalegrow.dy.bts.kafka.BtsInfoAddListener;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;

public class RecommendTest {

    @Test
    public void recommendTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("glb_fmd", "mr_T_3");
        map.put("glb_mrlc", "mp");
        System.out.println(getRecommendType(map));
    }


    private String getRecommendType(Map<String, Object> dataMap) {
        String fmd = String.valueOf(dataMap.get(BtsInfoAddListener.filedKeys.glb_fmd.name()));
        String mrlc = String.valueOf(dataMap.get(BtsInfoAddListener.filedKeys.glb_mrlc.name()));
        //this.logger.info("推荐位信息 fmd: {}, mrlc: {}", fmd, mrlc);
        String recommendType = "";
        if (StringUtils.isNotEmpty(fmd)) {
            recommendType =  RecommendTypeUtil.getRecommendTypeByFmd(fmd);
        }
        if (StringUtils.isEmpty(recommendType) && StringUtils.isNotEmpty(mrlc)) {
            recommendType = RecommendTypeUtil.getRecommendTypeByMrlc(mrlc);
        }
        if (StringUtils.isEmpty(recommendType)){
            String plf = String.valueOf(dataMap.get(BtsInfoAddListener.filedKeys.glb_plf.name()));
            String b = String.valueOf(dataMap.get(BtsInfoAddListener.filedKeys.glb_b.name()));
            return RecommendTypeUtil.getRecommendTypeByPlfAndB(plf, b);
        }
        return recommendType;
    }

    @Test
    public void testPassword() {
        System.out.println(new BASE64Encoder().encode("ADMIN:Dy@ai2018".getBytes()));
    }
}
