package com.globalegrow.dy.bts.kafka;

import com.globalegrow.common.hbase.CommonHbaseMapper;
import com.globalegrow.util.AppLogConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * a保存 app 埋点 device id 与 user id 的对应关系
 * customer_user_id，appsflyer_device_id
 */
//@Component
public class ZafulAppDeviceIdAndUserIdRelSave {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);

    @Value("${app.hbase.zfapp-userid-table}")
    private String hbaseTableName;

    @Value("${app.hbase.zfapp-user-clumn-family}")
    private String columnFamily;

    @Autowired
    private CommonHbaseMapper hbaseMapper;

    @KafkaListener(topics = {"${app.kafka.log-source-topic}"}, groupId = "dy_zaful_app_userid_save")
    public void listen(String log) {
        Map<String, Object> logMap = AppLogConvertUtil.getAppLogParameters(log);
        if (logMap != null && logMap.size() > 0) {
            String userId = String.valueOf(logMap.get("customer_user_id"));
            String deviceId = String.valueOf(logMap.get("appsflyer_device_id"));
            if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(deviceId)
                    && !"null".equals(userId) && !"null".equals(deviceId)) {
                this.logger.debug("保存设备id: {}, userId: {} 到 hbase", deviceId, userId);
                Map<String, Object> data = new HashMap<>();
                //data.put("cookie", cookie);
                data.put("userid", userId);
                this.hbaseMapper.insertData(this.hbaseTableName, data, deviceId,
                        this.columnFamily);
            }
        }
        this.countDownLatch1.countDown();
    }

}
