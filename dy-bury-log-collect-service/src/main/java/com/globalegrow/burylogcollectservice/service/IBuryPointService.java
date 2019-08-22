package com.globalegrow.burylogcollectservice.service;

import java.util.HashMap;

public interface IBuryPointService {
	/**
	 * 将埋点数据转化成map对象
	 * @param logLine
	 * @return
	 */
	HashMap<String, Object> logToMap(String logLine);
	/**
	 * 将埋点数据转化成json串
	 * @param logLine
	 * @return
	 */
	String logToJson(String logLine);
}
