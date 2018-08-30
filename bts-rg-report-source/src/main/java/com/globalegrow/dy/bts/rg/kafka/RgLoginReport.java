package com.globalegrow.dy.bts.rg.kafka;

import com.globalegrow.common.CommonBtsLogHandle;
import com.globalegrow.common.CommonLogModel;
import com.globalegrow.dy.bts.rg.model.BtsRgLoginReport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RgLoginReport extends CommonBtsLogHandle {

    @Value("${app.kafka.rg.login.topic}")
    private String loginReportTopic;

    /**
     * 最终输出的报表数据结构
     *
     * @param logMap
     * @return
     */
    @Override
    protected Map<String, Object> reportData(Map<String, Object> logMap) {
        Map<String, String> btsInfo = this.btsInfo(logMap);
        this.logger.debug("bts 实验信息: {}", btsInfo);
        if (btsInfo != null) {
            CommonLogModel commonLogModel = this.commonLogModel(logMap);
            String cookie = commonLogModel.getGlbOd();
            if (StringUtils.isNotEmpty(cookie)) {
                BtsRgLoginReport rgLoginReport = new BtsRgLoginReport();
                rgLoginReport.setBts(btsInfo);
                rgLoginReport.setSpecimen(cookie);
                rgLoginReport.setPageUv(cookie);
                switch (commonLogModel.getGlbX()) {
                    case "ZHUCE":
                        rgLoginReport.setSignUpClickUv(cookie);
                        break;
                    case "SIGNIN":
                        rgLoginReport.setSignInClickUv(cookie);
                        break;
                    case "SIGNINFB":
                        rgLoginReport.setSignInFbUv(cookie);
                        break;
                    case "SIGNINGG":
                        rgLoginReport.setSignInGoogleUv(cookie);
                        break;
                    case "FOGOTMM":
                        rgLoginReport.setForgetPasswdClickUv(cookie);
                        break;
                }
                return this.reportDataToMap(rgLoginReport);
            }

        }
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
        CommonLogModel commonLogModel = this.commonLogModel(logMap);
        if ("10007".equals(commonLogModel.getGlbD()) && "f01".equals(commonLogModel.getGlbS())
                && "pc".equals(commonLogModel.getGlbPlf())) {
            this.logger.debug("只处理 rosegal 网站登录注册页埋点数据");
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
        return this.loginReportTopic;
    }
}
