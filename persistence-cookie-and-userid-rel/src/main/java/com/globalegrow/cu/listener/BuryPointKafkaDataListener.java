package com.globalegrow.cu.listener;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.globalegrow.cu.constants.GlobalConstants;
import com.globalegrow.cu.hbase.CuRelDataEventHandler;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.SpringRedisUtil;

@Component
public class BuryPointKafkaDataListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${redis.bury-point-data.expireSeconds:86400}")
	private Long expireSeconds;

	@Autowired
	private CuRelDataEventHandler cuRelDataEventHandler;

	/**
	 * 埋点数据存放redis处理
	 * @param record
	 */
	@KafkaListener(topics = { "${app.kafka.log-json-topic}" }, groupId = "cookie_userid_get")
	public void listenerGetUser(ConsumerRecord<String, String> record) {
		String jsonLog = record.value();
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> mapJsonLog = JacksonUtil.readValue(jsonLog, Map.class);

			String cookie = String.valueOf(mapJsonLog.get("glb_od"));
			String userId = String.valueOf(mapJsonLog.get("glb_u"));
			String glbSku = String.valueOf(mapJsonLog.get("glb_sku"));
			String isOrder = String.valueOf(mapJsonLog.get("is_order"));
			String glbFmd = String.valueOf(mapJsonLog.get("glb_fmd"));
			if (StringUtils.isNotBlank(cookie) && !"getDeatilRecommend".equals(cookie)) {
				Map<String, Object> outJson = new HashMap<>();
				//满足推荐位加购事件
				boolean isFmdEvent = "1".equals(isOrder) && StringUtils.isNotBlank(glbSku) && StringUtils.isNotBlank(glbFmd)
						&& glbFmd.startsWith("mr_");
				if (StringUtils.isBlank(userId) && isFmdEvent) {
					logger.info("select rowKey familyColumn from hbase,cookie :{}", cookie);
					Object obj = this.cuRelDataEventHandler.selectRowKeyFamilyColumn(GlobalConstants.CU_TABLE_NAME, cookie,
							"user_id", GlobalConstants.COLUMN_FAMILY);
					if (obj != null) {
						userId = String.valueOf(obj);
						logger.info("select rowKey familyColumn from hbase, result userId:", userId);
					}
				}
				if (StringUtils.isNotBlank(userId) && isFmdEvent) {
					for (String k : GlobalConstants.LOG_FIELD_LIST) {
						outJson.put(k, mapJsonLog.get(k));
					}
					outJson.put("glb_u", userId);
					String redisKey = GlobalConstants.LOG_REDIS_KEY_START + glbSku + "_" + userId/* +"_"+tm */;
					String redisValue = JacksonUtil.toJSon(outJson);
					logger.info("data put redis,key :" + redisKey + ",value:{}" + redisValue);
					SpringRedisUtil.put(redisKey, redisValue, expireSeconds);
				}
			}

		} catch (Exception e) {
			//TODO 监控失败
			logger.error("json 转换出错: {}", jsonLog, e);
		}
	}

	/**
	 * 埋点数据存放hbase处理
	 * @param record
	 */
	@KafkaListener(topics = { "${app.kafka.log-json-topic}" }, groupId = "cookie_userid_put")
	public void listenerInsertUser(ConsumerRecord<String, String> record) {
		String jsonLog = record.value();
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> mapJsonLog = JacksonUtil.readValue(jsonLog, Map.class);
			String cookie = mapJsonLog.get("glb_od");
			String userId = mapJsonLog.get("glb_u");
			if (StringUtils.isNotBlank(cookie) && !"getDeatilRecommend".equals(cookie) && StringUtils.isNotBlank(userId)) {
				logger.info("cookie and userid relation insert hbase,cookie :{},userid:{}" + cookie, userId);
				Map<String, Object> data = new HashMap<>();
				data.put("user_id", userId);
				this.cuRelDataEventHandler.insertData(GlobalConstants.CU_TABLE_NAME, data, cookie,
						GlobalConstants.COLUMN_FAMILY);
			}
		} catch (Exception e) {
			logger.error("json 转换出错: {}", jsonLog, e);
		}
	}

}
