package com.globalegrow.test.data.generate.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.globalegrow.test.data.generate.constant.GlobalConstants;
import com.globalegrow.test.data.generate.service.IBuryPointService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("buryPointService")
public class BuryPointServiceImpl implements IBuryPointService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public HashMap<String, String> logToMap(String logLine) {
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
		Map<String, String> map = null;
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
	private HashMap<String, String> chgStrToMap(String[] spltLogLine, String requestStr) {
		HashMap<String, String> logMap = new HashMap<String, String>();
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
	private void processOtherParam(String[] spltLogLine, Map<String, String> logMap, Boolean isNotNeed) {
		if (isNotNeed) {
			logMap.clear();
		} else {
			logMap.put("remote_addr", spltLogLine[0].trim());
			logMap.put("remote_user", spltLogLine[1].trim());
			logMap.put("time_local", spltLogLine[2].trim());
			logMap.put("status", spltLogLine[4].trim());
			logMap.put("body_bytes_sent", spltLogLine[5].trim());
			logMap.put("http_referer", spltLogLine[6].trim());
			logMap.put("http_user_agent", spltLogLine[7].trim());
			logMap.put("http_x_forwarded_for", spltLogLine[8].trim());
			logMap.put("http_true_client_ip", spltLogLine[10].trim());
			logMap.put("geoip_city_country_code", spltLogLine[11].trim());
			logMap.put("geoip_country_name", spltLogLine[12].trim());
			logMap.put("http_accept_language", spltLogLine[13].trim());
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
	private Boolean processRequestParam(String requestStr, Map<String, String> logMap, Boolean isNotNeed) {
		// 通过正则表达是开始解析请求参数
		Matcher requestParamsMatcher = getMatcher(requestStr, GlobalConstants.PARAM_FORMAT);
		while (requestParamsMatcher.find()) {
			String key = requestParamsMatcher.group(1);
			/** 限定字段范围 **/
			if (GlobalConstants.LOG_FIELD_LIST.contains(key)) {
				String value = requestParamsMatcher.group(2);
				if ((GlobalConstants.SESSION_ID.equals(key) && StringUtils.isBlank(value))
						|| (GlobalConstants.BEHAVIOR_TYPE.equals(key) && StringUtils.isBlank(value))) {
					// sessionid或者点击类型为空，非法数据废弃
					isNotNeed = true;
					break;
				} else {
					if (StringUtils.isNotBlank(value)) {
						try {
							value = URLDecoder.decode(value,GlobalConstants.UTF_8);
						} catch (UnsupportedEncodingException e) {
							logger.error("BuryPointLogReceiver.chgStrToMap exception:{}", e);
						}
						// 处理双引号转义问题
						value = value.replace(GlobalConstants.QUOTE_X22, GlobalConstants.QUOTE);
					}
					logMap.put(key.toLowerCase(), value);
				}

			}
		}
		return isNotNeed;
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
	public static Matcher getMatcher(String event, String compile) {
		Pattern p = Pattern.compile(compile);
		Matcher m = p.matcher(event);
		return m;
	}

}
