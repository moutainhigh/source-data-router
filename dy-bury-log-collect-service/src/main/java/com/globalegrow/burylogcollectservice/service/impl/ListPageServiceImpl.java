package com.globalegrow.burylogcollectservice.service.impl;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.globalegrow.burylogcollectservice.common.constant.GlobalConstants;
import com.globalegrow.burylogcollectservice.service.IRecommendService;

@Service("listPageService")
public class ListPageServiceImpl implements IRecommendService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${dy.kafka.list.page.topic}")
	private String listPageTopic;

	@Value("${dy.kafka.list.page.topic.send}")
	private boolean listPageTopicSend;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void extField(Map<String, Object> map) {
		if (MapUtils.isNotEmpty(map)) {
			String value = (String) map.get(GlobalConstants.SOURCE_SITE_CODE);
			if (GlobalConstants.ZAFUL_CODE.equals(value)) {
				map.put("glb_ubcta_sku", "");
				map.put("is_repeat_data", 0);
				Integer isLpClick = 0;
				Integer isLpCart = 0;
				Integer isLpExp = 0;
				Integer isLpCollect = 0;
				String glbT = (String) map.get("glb_t");
				String glbX = (String) map.get("glb_x");
				String glbDc = (String) map.get("glb_dc");
				String glbFmd = (String) map.get("glb_fmd");
				String glbU = (String) map.get("glb_u");
				String glbS = (String) map.get("glb_s");
//				String glbFilterSort = (String) map.get("glb_filter_sort");
				String glbPm = (String) map.get("glb_pm");
				String glbUbctaSckw = (String) map.get("glb_ubcta_sckw");
				String glbUbcta = String.valueOf(map.get("glb_ubcta"));
				glbUbcta = "null".equals(glbUbcta) ? "" : glbUbcta;
				// glb_t = 'ie' AND glb_s = 'b01' AND glb_pm = 'mp' AND glb_ubcta <> '' and
				// glb_dc ='1301'
				// 由于数据源异常暂时去掉 && "1301".equals(glbDc)
				if ("ie".equals(glbT) && "b01".equals(glbS) && "mp".equals(glbPm)
						 && StringUtils.isNotBlank(glbUbcta)) {
					isLpExp = 1;
				}
				// glb_t = 'ic' AND glb_s = 'b01' and glb_pm = 'mp' AND glb_x in
				// ('sku','addtobag') and glb_dc ='1301'
				// 由于数据源异常暂时去掉 && "1301".equals(glbDc)
				if ("ic".equals(glbT) && "b01".equals(glbS) && "mp".equals(glbPm)
						&& ("sku".equals(glbX) || "addtobag".equals(glbX))) {
					isLpClick = 1;
				}
				// glb_t = 'ic' AND glb_x = 'ADT' AND glb_fmd = 'mp' AND glb_ubcta_sckw is null
				// and glb_dc ='1301'
				// 由于数据源异常暂时去掉 && "1301".equals(glbDc)
				if ("ic".equals(glbT) && "ADT".equals(glbX) && "mp".equals(glbFmd)
						&& StringUtils.isBlank(glbUbctaSckw)) {
					isLpCart = 1;
				}
				// glb_t = 'ic' AND glb_x = 'ADF' AND glb_fmd = 'mp' AND glb_u <>'' AND
				// glb_ubcta_sckw is null and glb_dc ='1301'
				// 由于数据源异常暂时去掉 && "1301".equals(glbDc)
				if ("ic".equals(glbT) && "ADF".equals(glbX) && "mp".equals(glbFmd)
						 && StringUtils.isNotBlank(glbU)&& StringUtils.isBlank(glbUbctaSckw)) {
					isLpCollect = 1;
				}
				map.put("is_lp_click", isLpClick);
				map.put("is_lp_exp", isLpExp);
				map.put("is_lp_cart", isLpCart);
				map.put("is_lp_collect", isLpCollect);
				this.processUbctaAndSendMsg(map, "glb_ubcta", glbUbcta);
			} else {
				map.clear();
			}
		}
	}

	/**
	 * 与点击或曝光事件相关的事件属性
	 * 
	 * @param logMap
	 * @param key
	 * @param value
	 */
	private void processUbctaAndSendMsg(Map<String, Object> logMap, String key, String value) {
		Object object = null;
		try {
			JSONArray array = this.doJsonArray(logMap, value);
			object = array;
		} catch (Exception e) {
			// logger.warn("Ubcta is not an array.", e);c
		}
		if (object == null) {
			this.doJsonObject(logMap, value);
		}
	}

	private void doJsonObject(Map<String, Object> logMap, String value) {
		JSONObject jSONObject = null;
		try {
			jSONObject = JSONObject.parseObject(value);
		} catch (Exception e) {
			// logger.error("not json object.value:{}",value);need not do nothing
		}
		if (jSONObject != null) {
			String skuValue = String.valueOf(jSONObject.get("sku"));
			skuValue = StringUtils.equals(skuValue, "null") ? "" : skuValue;
			logMap.put("glb_ubcta_sku", skuValue);
			if (listPageTopicSend) {
				logMap.put("glb_osr_referrer", "");
				logMap.put("glb_osr_landing", "");
				logMap.put("body_bytes_sent", "");
				logMap.put("glb_w", "");
				logMap.put("glb_filter", "");
				logMap.put("glb_skuinfo", "");
				logMap.put("remote_user", "");
				logMap.put("glb_skuinfos", "");
				logMap.put("glb_osr", "");	
				logMap.put("glb_pagemodule", "");
				logMap.put("glb_od", "");
				logMap.put("glb_ksku", "");
				logMap.put("glb_oi", "");
				logMap.put("remote_addr", "");
				logMap.put("glb_ubcta", "");
				logMap.put("glb_cl", "");
				logMap.put("time_local", "");
				logMap.put("glb_siws", "");
				logMap.put("glb_pl", "");
				logMap.remove("http_user_agent");
				logMap.remove("http_true_client_ip");
				logMap.remove("http_accept_language");
				logMap.remove("http_referer");
				logMap.remove("http_x_forwarded_for");
				logMap.remove("exposure_count");
				this.sendToKafka(logMap, listPageTopic);
			}
		}
	}

	private JSONArray doJsonArray(Map<String, Object> logMap, String value) {
		JSONArray array = JSONArray.parseArray(value);
		if (CollectionUtils.isNotEmpty(array)) {
			// 为避免数据重复初曝光量以外其他只取一条
			int skuNum = 1;
			for (Object jsonObject : array) {
				JSONObject jSONObject = (JSONObject) jsonObject;
				String skuValue = String.valueOf(jSONObject.get("sku"));
				skuValue = StringUtils.equals(skuValue, "null") ? "" : skuValue;
				if (skuNum == 1) {
					logMap.put("is_repeat_data", 0);
				} else {
					logMap.put("is_repeat_data", 1);
					logMap.put("is_lp_click", 0);
					logMap.put("is_lp_cart", 0);
					logMap.put("is_lp_collect", 0);
				}
				logMap.put("glb_ubcta_sku", skuValue);
				if (listPageTopicSend) {
					logMap.put("glb_osr_referrer", "");
					logMap.put("glb_osr_landing", "");
					logMap.put("body_bytes_sent", "");
					logMap.put("glb_w", "");
					logMap.put("glb_filter", "");
					logMap.put("glb_skuinfo", "");
					logMap.put("remote_user", "");
					logMap.put("glb_skuinfos", "");
					logMap.put("glb_osr", "");	
					logMap.put("glb_pagemodule", "");
					logMap.put("glb_od", "");
					logMap.put("glb_ksku", "");
					logMap.put("glb_oi", "");
					logMap.put("remote_addr", "");
					logMap.put("glb_ubcta", "");
					logMap.put("glb_cl", "");
					logMap.put("time_local", "");
					logMap.put("glb_siws", "");
					logMap.put("glb_pl", "");
					logMap.remove("http_user_agent");
					logMap.remove("http_true_client_ip");
					logMap.remove("http_accept_language");
					logMap.remove("http_referer");
					logMap.remove("http_x_forwarded_for");
					logMap.remove("exposure_count");
					this.sendToKafka(logMap, listPageTopic);
				}
				skuNum++;
			}
		}
		return array;
	}

	private void sendToKafka(Map<String, Object> logMap, String topic) {
		String message = JSONObject.toJSONString(logMap);
		if (StringUtils.isNotBlank(message)) {
			this.kafkaTemplate.send(topic, message);
			logger.info("send to dy kafak success.topic:{}:message:{}", topic, message);
		}
	}

}
