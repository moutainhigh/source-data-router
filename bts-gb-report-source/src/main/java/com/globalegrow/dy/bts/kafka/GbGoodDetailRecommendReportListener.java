package com.globalegrow.dy.bts.kafka;

import com.globalegrow.bts.model.BtsGbRecommendReport;
import com.globalegrow.common.CommonBtsLogHandle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
public class GbGoodDetailRecommendReportListener extends CommonBtsLogHandle {

    @Value("${app.kafka.gb-good-detail-rec-topic}")
    private String goodDetailRecommendReportTopic;

    /**
     * 最终输出的报表数据结构
     *
     * @param logMap
     * @return
     */
    @Override
    protected Map<String, Object> reportData(Map<String, Object> logMap) {
        Map<String, String> btsInfo = this.btsInfo(logMap);
        if (btsInfo != null) {
            String glbX = String.valueOf(logMap.get("glb_x"));
            String glbOd = String.valueOf(logMap.get("glb_od"));
            String glbT = String.valueOf(logMap.get("glb_t"));
            String glbU = String.valueOf(logMap.get("glb_u"));
            String glbSkuInfo = String.valueOf(logMap.get("glb_skuinfo"));
            String glbUbcta = String.valueOf(logMap.get("glb_ubcta"));
            String glbPm = String.valueOf(logMap.get("glb_pm"));
            String glbB = String.valueOf(logMap.get("glb_b"));

            boolean isGoodDetail = "c".equals(glbB);
            if (isGoodDetail) {
                this.logger.debug("处理商详页埋点数据");
                BtsGbRecommendReport btsGbRecommendReport = new BtsGbRecommendReport();
            }

        }
        this.logger.debug("埋点中未找到 bts 实验信息: {}", logMap);
        return null;
    }

    /**
     * 报表数据过滤，如站点过滤等
     *
     * @param logMap
     * @return
     */
    @Override
    protected boolean logDataFilter(Map<String, Object> logMap) {
        if ("10002".equals(logMap.get("glb_d"))) {
            this.logger.debug("只处理 gb 网站埋点数据");
            return true;
        }
        return false;
    }

    /**
     * 报表输出 topic
     *
     * @return
     */
    @Override
    protected String reportKafkaTopic() {
        return this.goodDetailRecommendReportTopic;
    }
}
