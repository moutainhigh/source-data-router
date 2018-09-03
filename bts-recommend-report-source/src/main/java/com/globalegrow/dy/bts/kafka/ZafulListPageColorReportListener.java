package com.globalegrow.dy.bts.kafka;

import com.globalegrow.common.CommonLogModel;
import com.globalegrow.common.CommonModelBtsLogHandle;
import com.globalegrow.dy.bts.model.BtsZafulListPageColorReport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
public class ZafulListPageColorReportListener extends CommonModelBtsLogHandle {

    @Value("${app.kafka.zaful-list-page-color-topic}")
    private String listColorReportTopoc;

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch2 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch3 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch4 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch5 = new CountDownLatch(1);
    /**
     * 最终输出的报表数据结构
     *
     * @param commonLogModel
     * @return
     */
    @Override
    protected Map<String, Object> reportData(CommonLogModel commonLogModel) {
        Map<String, String> btsInfo = this.btsInfo(commonLogModel);
        this.logger.debug("bts 实验信息: {}", btsInfo);
        if (btsInfo != null) {
            BtsZafulListPageColorReport colorReport = new BtsZafulListPageColorReport();
            colorReport.setBts(btsInfo);
            String cookie = commonLogModel.getGlbOd();
            colorReport.setSpecimen(cookie);
            colorReport.setPageUv(cookie);
            if ("ic".equals(commonLogModel.getGlbT())) {
                if ("sku".equals(commonLogModel.getGlbX())) {
                    this.logger.debug("report 商品点击事件");
                    colorReport.setGoodClickUv(cookie);
                }
                else if ("ADF".equals(commonLogModel.getGlbX())) {
                    this.logger.debug("report 商品收藏点击事件");
                    colorReport.setGoodCollectUv(cookie);
                }
            }
            else if ("ie".equals(commonLogModel.getGlbT())) {
                if (StringUtils.isEmpty(commonLogModel.getGlbUbcta())) {
                    this.logger.debug("report 列表停留时间");
                    colorReport.setPageStayMillSecs(commonLogModel.getGlbW());
                }
            }
            return this.reportDataToMap(colorReport);
        }
        return null;
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"6","7"})}, groupId = "bts_zaful_list_color_report")
    public void listen1(String logString) throws Exception {
        this.logger.debug("customer thread 1");
        this.handleLogData(logString);
        this.countDownLatch1.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"8","9"})}, groupId = "bts_zaful_list_color_report")
    public void listen2(String logString) throws Exception {
        this.logger.debug("customer thread 2");
        this.handleLogData(logString);
        this.countDownLatch2.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"4","5"})}, groupId = "bts_zaful_list_color_report")
    public void listen3(String logString) throws Exception {
        this.logger.debug("customer thread 3");
        this.handleLogData(logString);
        this.countDownLatch3.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"2","3"})}, groupId = "bts_zaful_list_color_report")
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
    //@KafkaListener(topics = {"${app.kafka.log-source-topic}"}, groupId = "bts_zaful_list_color_report")
    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"0","1"})}, groupId = "bts_zaful_list_color_report")
    public void listen5(String logString) throws Exception {
        this.logger.debug("customer thread 5");
        this.handleLogData(logString);
        this.countDownLatch5.countDown();
    }

    /**
     * 报表数据过滤，如站点过滤等
     *
     * @param commonLogModel
     * @return
     */
    @Override
    protected boolean logDataFilter(CommonLogModel commonLogModel) {
        return "b01".equals(commonLogModel.getGlbS()) && "pc".equals(commonLogModel.getGlbPlf()) && "1301".equals(commonLogModel.getGlbDc()) && "10013".equals(commonLogModel.getGlbD());
    }

    /**
     * 报表输出 topic
     *
     * @return
     */
    @Override
    protected String reportKafkaTopic() {
        return this.listColorReportTopoc;
    }
}
