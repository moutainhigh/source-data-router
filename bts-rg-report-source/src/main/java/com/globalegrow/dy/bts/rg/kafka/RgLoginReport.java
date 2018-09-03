package com.globalegrow.dy.bts.rg.kafka;

import com.globalegrow.common.CommonBtsLogHandle;
import com.globalegrow.common.CommonLogModel;
import com.globalegrow.dy.bts.rg.model.BtsRgLoginReport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
public class RgLoginReport extends CommonBtsLogHandle {

    @Value("${app.kafka.rg.login.topic}")
    private String loginReportTopic;

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch2 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch3 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch4 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch5 = new CountDownLatch(1);

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

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"6", "7"})}, groupId = "bts_rg_login_report")
    public void listen1(String logString) throws Exception {
        this.logger.debug("customer thread 1");
        this.handleLogData(logString);
        this.countDownLatch1.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"8", "9"})}, groupId = "bts_rg_login_report")
    public void listen2(String logString) throws Exception {
        this.logger.debug("customer thread 2");
        this.handleLogData(logString);
        this.countDownLatch2.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"4", "5"})}, groupId = "bts_rg_login_report")
    public void listen3(String logString) throws Exception {
        this.logger.debug("customer thread 3");
        this.handleLogData(logString);
        this.countDownLatch3.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"2", "3"})}, groupId = "bts_rg_login_report")
    public void listen4(String logString) throws Exception {
        this.logger.debug("customer thread 4");
        this.handleLogData(logString);
        this.countDownLatch4.countDown();
    }

    /**
     * {"plancode":"rgcart","versionid":"47","bucketid":"9","planid":"20","policy":"1"}
     *
     * @throws Exception
     */
    //@KafkaListener(topics = {"${app.kafka.log-source-topic}"}, groupId = "bts_rg_login_report")
    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"0", "1"})}, groupId = "bts_rg_login_report")
    public void listen5(String logString) throws Exception {
        this.logger.debug("customer thread 5");
        this.handleLogData(logString);
        this.countDownLatch5.countDown();
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
