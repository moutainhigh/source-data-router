package com.globalegrow.report;

import com.globalegrow.common.hbase.AbstractHbaseDataPersistence;
import com.globalegrow.util.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户 id cookie 关系保存
 */
//@Component
public class CookieAndUserIHbasedRel extends AbstractHbaseDataPersistence {

    public static final String HBASE_TABLE_NAME = "dy_cookie_userid_rel";

    public static final String HTABLE_COLUMN_FAMILY = "cookie_userid";

    @KafkaListener(topics = { "glbg-analitic-json-pc" }, groupId = "cookie_userid_get_pc_m")
    public void listenerGetUserPc(ConsumerRecord<String, String> record) {
        try {
            Map<String,Object> m = JacksonUtil.readValue(record.value(), Map.class);
            if (m.get("glb_u") != null && StringUtils.isNotEmpty(String.valueOf(m.get("glb_u")))) {
                String userId = String.valueOf(m.get("glb_u"));
                String id = String.valueOf(m.get("glb_od")) + "_" + String.valueOf(m.get("glb_d")) + "_" + String.valueOf(m.get("glb_dc"));
                Map<String, Object> data = new HashMap<>();
                data.put("userid", userId);
                this.insertData(HBASE_TABLE_NAME, data, id, HTABLE_COLUMN_FAMILY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = { "glbg-analitic-json-app" }, groupId = "cookie_userid_get_app")
    public void listenerGetUserApp(ConsumerRecord<String, String> record) {
        try {
            Map<String,Object> m = JacksonUtil.readValue(record.value(), Map.class);
            if (m.get("customer_user_id") != null && StringUtils.isNotEmpty(String.valueOf(m.get("customer_user_id")))
                    && !"0".equals(String.valueOf(m.get("customer_user_id")))) {
                String userId = String.valueOf(m.get("customer_user_id"));
                Map<String, Object> data = new HashMap<>();
                data.put("userid", userId);
                String id = String.valueOf(m.get("appsflyer_device_id")) + "_" + String.valueOf(m.get("app_name")) + "_" + String.valueOf(m.get("platform"));
                this.insertData(HBASE_TABLE_NAME, data, id, HTABLE_COLUMN_FAMILY);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
