package com.globalegrow.burylogcollectservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.globalegrow.burylogcollectservice.common.constant.GlobalConstants;
import com.globalegrow.burylogcollectservice.service.IBuryPointService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("buryPointService")
public class BuryPointServiceImpl implements IBuryPointService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Value("${test.isopentestlog}")
	private Boolean isOpenTestLog;
	@Value("${stat.interval.minutes}")
	private int statIntervalMinutes;

	public HashMap<String, Object> logToMap(String logLine) {
		String requestStr = this.getRequestStr(logLine);
		String[] spltLogLine = logLine.split(GlobalConstants.LOG_DIVISION);
		// 请求有参数且埋点数据经过分割后长度大于等于14消息才有效
		if (StringUtils.isNotBlank(requestStr) && spltLogLine.length >= 14) {
			return this.chgStrToMap(spltLogLine, requestStr);
		} else {
			return null;
		}
	}

	public String logToJson(String logLine) {
		String requestStr = this.getRequestStr(logLine);
		String[] spltLogLine = logLine.split(GlobalConstants.LOG_DIVISION);
		Map<String, Object> map = null;
		// 请求有参数且埋点数据经过分割后长度大于等于14消息才有效
		if (StringUtils.isNotBlank(requestStr) && spltLogLine.length >= 14) {
			map = this.chgStrToMap(spltLogLine, requestStr);
		}
		if (MapUtils.isNotEmpty(map)) {
			return JSONObject.toJSONString(map);
		} else {
			return null;
		}
	}

	/**
	 * 切割节点数据，构建map格式
	 * 
	 * @param spltLogLine
	 * @param requestStr
	 * @throws Exception
	 */
	private HashMap<String, Object> chgStrToMap(String[] spltLogLine, String requestStr) {
		HashMap<String, Object> logMap = new HashMap<String, Object>();
		/** 初始化map字段begin **/
		for (String key : GlobalConstants.LOG_FIELD_LIST) {
			logMap.put(key.toLowerCase(), "");
		}
		// 推荐位
		logMap.put("glb_mrlc", "");
		// 曝光数量
		logMap.put("exposure_count", 0);
		logMap.put("versionid", 0);
		logMap.put("planid", 0);
		logMap.put("bucketid", 0);
		logMap.put("glb_fmd", "");
		logMap.put("glb_sku", "");
		logMap.put("glb_filter_sort", "");
		logMap.put("glb_skuinfo_pam", 0);
		logMap.put("glb_ubcta_sckw", "");
		logMap.put("cat_id", "");
		/** 初始化map字段end **/
		Boolean isNotNeed = false;
		// 解析url参数
		isNotNeed = this.processRequestParam(requestStr, logMap, isNotNeed);
		// 解析埋点的其他参数
		this.processOtherParam(spltLogLine, logMap, isNotNeed);
		return logMap;
	}

	/**
	 * 解析埋点其他参数
	 * 
	 * @param spltLogLine
	 * @param logMap
	 * @param isNotNeed
	 */
	private void processOtherParam(String[] spltLogLine, Map<String, Object> logMap, Boolean isNotNeed) {
		if (isNotNeed) {
			logMap.clear();
		} else {
			logMap.put("remote_addr", spltLogLine[0].trim());
			logMap.put("remote_user", spltLogLine[1].trim());
			logMap.put("time_local", spltLogLine[2].trim());
			logMap.put("status", spltLogLine[4].trim());
			logMap.put("body_bytes_sent", spltLogLine[5].trim());
			logMap.put("http_referer", StringUtils.replace(spltLogLine[6].trim(), GlobalConstants.QUOTE, ""));
			logMap.put("http_user_agent", StringUtils.replace(spltLogLine[7].trim(), GlobalConstants.QUOTE, ""));
			logMap.put("http_x_forwarded_for", spltLogLine[8].trim());
			logMap.put("http_true_client_ip", spltLogLine[10].trim());
			logMap.put("geoip_city_country_code", spltLogLine[11].trim());
			logMap.put("geoip_country_name", spltLogLine[12].trim());
			logMap.put("http_accept_language", spltLogLine[13].trim());

			// 修改时间戳为服务器的时间			
			String tm = spltLogLine[spltLogLine.length-1]+"000";
			logMap.put("glb_tm",tm);
			
			try {
				logMap.put("stat_group_minutes", this.processTimeGroup(Long.valueOf(tm)));
			} catch (Exception e) {
				logMap.clear();
			}
		}
	}

	/**
	 * 解析埋点中的url参数
	 * 
	 * @param requestStr
	 * @param logMap
	 * @param isNotNeed
	 * @return
	 */
	private Boolean processRequestParam(String requestStr, Map<String, Object> logMap, Boolean isNotNeed) {
		// 通过正则表达是开始解析请求参数
		Matcher requestParamsMatcher = getMatcher(requestStr, GlobalConstants.PARAM_FORMAT);
		while (requestParamsMatcher.find()) {
			String key = requestParamsMatcher.group(1);
			/** 限定字段范围 **/
			if (GlobalConstants.LOG_FIELD_LIST.contains(key)) {
				key = key.toLowerCase();
				String value = requestParamsMatcher.group(2);
				if ((GlobalConstants.SESSION_ID.equals(key) && StringUtils.isBlank(value))
						|| (GlobalConstants.BEHAVIOR_TYPE.equals(key) && StringUtils.isBlank(value))) {
					// sessionid或者点击类型为空，非法数据废弃
					isNotNeed = true;
					break;
				} else {
					// 处理参数值并放入map
					this.processParamValue(logMap, key, value);
				}
			}
		}
		return isNotNeed;
	}

	private void processParamValue(Map<String, Object> logMap, String key, String value) {
		if (StringUtils.isNotBlank(value)) {
			try {
				value = value.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
				value = URLDecoder.decode(value, GlobalConstants.UTF_8);
			} catch (UnsupportedEncodingException e) {
				logger.error("BuryPointLogReceiver.chgStrToMap exception:{}", e);
			}
			// 处理双引号转义问题
			value = value.replace(GlobalConstants.QUOTE_X22, GlobalConstants.QUOTE);
		}

		if ("glb_ubcta".equals(key)) {
			this.processUbcta(logMap, key, value);
		} else {
			if ("glb_skuinfo".equals(key)) {
				logMap.put("glb_skuinfo", JSONObject.parse(value));
				if(value!=null) {
					this.processSkuinfo(logMap, key, value);
				}			
			} else {
				logMap.put(key, value);
			}
		}
		if ("glb_filter".equals(key)) {
			try {
				JSONObject jSONObject = JSONObject.parseObject(value);
				String sortValue = String.valueOf(jSONObject.get("sort"));
				sortValue = StringUtils.equals(sortValue, "null") ? "" : sortValue;
				logMap.put("glb_filter_sort", sortValue);
			} catch (Exception e) {
				logger.error("glb_filter parseObject error:", e);
			}
		}
		//这里一直在报错，估计是底层格式改了，但是没有人知道这个干嘛的，也不好处理。猜测注释调应该没有问题，这里报错越界和null
		// ，其实也是解析不出数据，那么不设置这个也没有问题。总数没有数据
//		if ("glb_p".equals(key)) {
//			if(StringUtils.isNotBlank(value)) {
//				String [] glbPs = value.split("-");
//				logMap.put("cat_id", glbPs[1]);
//			}
//		}
	}

	private void processSkuinfo(Map<String, Object> logMap, String key, String value) {
		try {
			JSONObject jSONObject = JSONObject.parseObject(value);
			String skuValue = String.valueOf(jSONObject.get("sku"));
			skuValue = StringUtils.equals(skuValue, "null") ? "" : skuValue;
			logMap.put("glb_sku", skuValue);
			String pamValue = String.valueOf(jSONObject.get("pam"));
			try {
				int pamV = StringUtils.equals(pamValue, "null") ? 0 : Integer.valueOf(pamValue);
				logMap.put("glb_skuinfo_pam", pamV);
			} catch (Exception e) {
				logger.error("Integer.valueOf(pamValue)", e);
			}				
		} catch (Exception e) {
//			logger.error("skuinfo is an array or null.", e);
		}
	}

	/**
	 * 与点击或曝光事件相关的事件属性
	 * 
	 * @param logMap
	 * @param key
	 * @param value
	 */
	private void processUbcta(Map<String, Object> logMap, String key, String value) {
		Object object = null;

		try {
			JSONArray array = this.doJsonArray(logMap, value);
			object = array;
		} catch (Exception e) {
			//logger.warn("Ubcta is not an array.", e);		
		}
		if (object == null) {
			object = this.doJsonObject(logMap, value);
		}
		logMap.put(key, object);
	}

	private Object doJsonObject(Map<String, Object> logMap, String value) {
		Object object;
		int exposureCount = 0;
		JSONObject jSONObject = null;
		try {
			jSONObject = JSONObject.parseObject(value);
		}catch(Exception e) {
			//logger.error("not json object.value:{}",value);need not do nothing	
		}
		if(jSONObject!=null) {
			String mrlcValue = String.valueOf(jSONObject.get("mrlc"));
			mrlcValue = StringUtils.equals(mrlcValue, "null") ? "" : mrlcValue;
			String fmdValue = String.valueOf(jSONObject.get("fmd"));
			fmdValue = StringUtils.equals(fmdValue, "null") ? "" : fmdValue;
			if (StringUtils.isNotBlank(mrlcValue) && StringUtils.isNotBlank(String.valueOf(jSONObject.get("sku")))) {
				exposureCount++;
			}
			String sckwValue = String.valueOf(jSONObject.get("sckw"));
			sckwValue = StringUtils.equals(sckwValue, "null") ? "" : sckwValue;
			logMap.put("glb_ubcta_sckw", sckwValue);
			// 推荐位
			logMap.put("glb_mrlc", mrlcValue);
			// 曝光数量
			logMap.put("exposure_count", exposureCount);
			logMap.put("glb_fmd", fmdValue);
		}	
		object = jSONObject;
		return object;
	}

	private JSONArray doJsonArray(Map<String, Object> logMap, String value) {
		JSONArray array = JSONArray.parseArray(value);
		if (CollectionUtils.isNotEmpty(array)) {
			String mrlcValue = "";
			String fmdValue = "";
			int exposureCount = 0;
			for (Object jsonObject : array) {
				JSONObject jSONObject = (JSONObject) jsonObject;
				mrlcValue = String.valueOf(jSONObject.get("mrlc"));
				mrlcValue = StringUtils.equals(mrlcValue, "null") ? "" : mrlcValue;
				fmdValue = String.valueOf(jSONObject.get("fmd"));
				fmdValue = StringUtils.equals(fmdValue, "null") ? "" : fmdValue;
				if (StringUtils.isNotBlank(mrlcValue)
						&& StringUtils.isNotBlank(String.valueOf(jSONObject.get("sku")))) {
					exposureCount++;
				}
				String sckwValue = String.valueOf(jSONObject.get("sckw"));
				sckwValue = StringUtils.equals(sckwValue, "null") ? "" : sckwValue;
				logMap.put("glb_ubcta_sckw", sckwValue);
			}
			// 推荐位
			logMap.put("glb_mrlc", mrlcValue);
			// 曝光数量
			logMap.put("exposure_count", exposureCount);
			//
			logMap.put("glb_fmd", fmdValue);
		}
		return array;
	}

	/**
	 * 截取请求串
	 * 
	 * @param value
	 * @return
	 */
	private String getRequestStr(String value) {
		Matcher requestStrMatcher = getMatcher(value, "_ubc.gif\\??(.*)HTTP");
		String requestStr = "";

		while (requestStrMatcher.find()) {
			requestStr = requestStrMatcher.group(1);
		}

		return StringUtils.isNotBlank(requestStr) ? StringUtils.trim(requestStr) + "&" : "";
	}

	/**
	 * @Title: getMatcher @Description: TODO(获取 compile格式的 matcher) @param
	 *         event @param compile @param @return Matcher 返回类型 @throws
	 */
	private Matcher getMatcher(String event, String compile) {
		Pattern p = Pattern.compile(compile);
		Matcher m = p.matcher(event);
		return m;
	}

	private Long processTimeGroup(Long tm) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tm);

		int minute = calendar.get(Calendar.MINUTE);
		if (minute % 15 != 0) {
			int j = minute / 15;
			if (j == 0) {
				calendar.set(Calendar.MINUTE, 15);
			} else if (j == 1) {
				calendar.set(Calendar.MINUTE, 30);
			} else if (j == 2) {
				calendar.set(Calendar.MINUTE, 45);
			} else if (j == 3) {
				calendar.add(Calendar.HOUR, 1);
				calendar.set(Calendar.MINUTE, 0);
			}
		}
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}
}
