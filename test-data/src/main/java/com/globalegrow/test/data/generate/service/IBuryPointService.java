package com.globalegrow.test.data.generate.service;

import java.util.HashMap;

public interface IBuryPointService {
	/**
	 * 将埋点数据转化成map对象
	 * @param logLine
	 * @return
	 */
	HashMap<String, String> logToMap(String logLine);
	/**
	 * 将埋点数据转化成json串
	 * @param logLine
	 * @return
	 */
	String logToJson(String logLine);
}
