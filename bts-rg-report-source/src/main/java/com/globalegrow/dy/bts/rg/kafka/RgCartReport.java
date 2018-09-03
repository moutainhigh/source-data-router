package com.globalegrow.dy.bts.rg.kafka;

import com.globalegrow.dy.bts.rg.enums.BtsFields;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

//@Component
public class RgCartReport {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch2 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch3 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch4 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch5 = new CountDownLatch(1);

    @Value("${app.kafka.rg.cart.topic}")
    public String rgCartReportTopic;

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"6","7"})}, groupId = "bts_rg_cart_report")
    public void listen1(String logString) throws Exception {
        this.logger.debug("customer thread 1");
        this.handleMsg(logString);
        this.countDownLatch1.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"8","9"})}, groupId = "bts_rg_cart_report")
    public void listen2(String logString) throws Exception {
        this.logger.debug("customer thread 2");
        this.handleMsg(logString);
        this.countDownLatch2.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"4","5"})}, groupId = "bts_rg_cart_report")
    public void listen3(String logString) throws Exception {
        this.logger.debug("customer thread 3");
        this.handleMsg(logString);
        this.countDownLatch3.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"2","3"})}, groupId = "bts_rg_cart_report")
    public void listen4(String logString) throws Exception {
        this.logger.debug("customer thread 4");
        this.handleMsg(logString);
        this.countDownLatch4.countDown();
    }

    /**
     * {"plancode":"rgcart","versionid":"47","bucketid":"9","planid":"20","policy":"1"}
     *
     * @throws Exception
     */
    //@KafkaListener(topics = {"${app.kafka.log-source-topic}"}, groupId = "bts_rg_cart_report")
    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"0","1"})}, groupId = "bts_rg_cart_report")
    public void listen5(String logString) throws Exception {
        this.logger.debug("customer thread 5");
        this.handleMsg(logString);
        this.countDownLatch5.countDown();
    }

    private void handleMsg(String logString) throws Exception {
        Map<String, Object> logMap = NginxLogConvertUtil.getNginxLogParameters(logString);
        this.logger.debug("转换后的日志数据: {}", logMap);
        if (logMap != null && logMap.size() > 0) {
            if ("10007".equals(logMap.get("glb_d"))) {
                this.logger.debug("只处理 rg 站点的埋点数据");
                String btsInfo = String.valueOf(logMap.get("glb_bts"));
                this.logger.debug("bts 实验信息: {}", btsInfo);
                if (btsInfo != null && StringUtils.isNotBlank(btsInfo) && !"null".equals(btsInfo)) {
                    Map<String, Object> cartReportMap = new HashMap<>();
                    cartReportMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, logMap.get(NginxLogConvertUtil.TIMESTAMP_KEY));
                    this.logger.debug("处理 bts 信息 bts: {}, logInfo: {}", btsInfo, logMap);
                    Map<String, String> btsInfoMap = GsonUtil.readValue(btsInfo, Map.class);
                    if (btsInfoMap != null) {

                        Map<String, String> btsReportMap = new HashMap<>();
                        btsReportMap.put(BtsFields.plan_id.name(), btsInfoMap.get("planid"));
                        btsReportMap.put(BtsFields.version_id.name(), btsInfoMap.get("versionid"));
                        btsReportMap.put(BtsFields.bucket_id.name(), btsInfoMap.get("bucketid"));
                        cartReportMap.put("bts", btsReportMap);
                        String policy = btsInfoMap.get("policy");
                        String glbS = String.valueOf(logMap.get("glb_s"));
                        String glbPlf = String.valueOf(logMap.get("glb_plf"));
                        String glbX = String.valueOf(logMap.get("glb_x"));
                        String glbOd = String.valueOf(logMap.get("glb_od"));
                        String glbCl = String.valueOf(logMap.get("glb_cl"));
                        this.logger.debug("glb_s: {}, ", glbS);
                        if ("f01".equals(glbS) || "d01".equals(glbS) || "d02".equals(glbS) || "d03".equals(glbS)) {
                            // policy
                            cartReportMap.put("policy", policy);

                            // 页面 uv
                            //cartReportMap.put("glb_od", glbOd);
                            // 购物车 uv
                            // 购物车 uv
                            if ("d01".equals(glbS) && "pc".equals(glbPlf)) {
                                this.logger.debug("购物车 uv, {}", glbOd);
                                cartReportMap.put("cart_uv", glbOd);
                            }else {
                                cartReportMap.put("cart_uv", null);
                            }
                            // check out 点击数 & uv
                            if ("d01".equals(glbS) && "pc".equals(glbPlf) && "checkout".equals(glbX)) {
                                this.logger.debug("check out uv, {}", glbOd);
                                cartReportMap.put("check_out_click_num", 1);
                                cartReportMap.put("check_out_uv", glbOd);
                            } else {
                                cartReportMap.put("check_out_click_num", 0);
                                cartReportMap.put("check_out_uv", null);
                            }
                            // 登录点击数 & uv
                            if (("f01".equals(glbS) && "pc".equals(glbPlf) && "SIGNIN".equals(glbX)
                                    && "https://login.rosegal.com/m-users-a-sign.htm?flow=checkout".equals(glbCl))
                                    || ("d01".equals(glbS) && "pc".equals(glbPlf) && "SIGNIN".equals(glbX))) {
                                this.logger.debug("sign in uv, {}, policy: {}", glbOd, policy);
                                cartReportMap.put("cart_login_click", 1);

                                cartReportMap.put("cart_login_uv", glbOd);
                            } else {
                                cartReportMap.put("cart_login_click", 0);
                                cartReportMap.put("cart_login_uv", null);
                            }

                            /*if ("0".equals(policy) && "f01".equals(glbS)) {
                                this.logger.debug("sign_in_0:{}, s: {},plf: {}, Map: {}", glbCl, glbS, glbPlf, logMap);
                            }*/

                            // 注册点击数 & UV
                            if (("f01".equals(glbS) && "pc".equals(glbPlf) && "ZHUCE".equals(glbX)
                                    && "https://login.rosegal.com/m-users-a-sign.htm?flow=checkout".equals(glbCl))
                                    || ("d01".equals(glbS) && "pc".equals(glbPlf) && "ZHUCE".equals(glbX))) {
                                this.logger.debug("sign up uv, {}, policy: {}", glbOd, policy);
                                cartReportMap.put("cart_sign_up_click", 1);

                                cartReportMap.put("cart_sign_up_uv", glbOd);

                            } else {
                                cartReportMap.put("cart_sign_up_click", 0);
                                cartReportMap.put("cart_sign_up_uv", null);
                            }

                            // 地址页 uv
                            if ("d02".equals(glbS) && "pc".equals(glbPlf)) {
                                cartReportMap.put("address_page_uv", glbOd);
                            } else {
                                cartReportMap.put("address_page_uv", null);
                            }

                            // 订单确认页 uv
                            if ("d03".equals(glbS) && "pc".equals(glbPlf)) {
                                cartReportMap.put("order_confirm_uv", glbOd);
                            } else {
                                cartReportMap.put("order_confirm_uv", null);
                            }

                            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(rgCartReportTopic, JacksonUtil.toJSon(cartReportMap));
                            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                                @Override
                                public void onSuccess(SendResult<String, String> result) {
                                    logger.debug("log json with dimensions send success!");
                                }

                                @Override
                                public void onFailure(Throwable ex) {
                                    logger.error("log json with dimensions send failed! msg: {}", cartReportMap, ex);
                                }
                            });
                        }

                    }

                } else {
                    this.logger.info(" rg 站埋点中无 bts 信息: {}", logMap);
                }
            }

        }
    }

}
