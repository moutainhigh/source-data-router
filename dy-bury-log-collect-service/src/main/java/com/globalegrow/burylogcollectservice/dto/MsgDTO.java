package com.globalegrow.burylogcollectservice.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MsgDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3948826438942105561L;
	/**
	 * 主题
	 */
	private List<String> topics;
	/**
	 * 消息
	 */
	private Map<String, Object> logMap;
	public MsgDTO() {
		super();
	}
	
	public MsgDTO(List<String> topics, Map<String, Object> logMap) {
		super();
		this.topics = topics;
		this.logMap = logMap;
	}

	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public Map<String, Object> getLogMap() {
		return logMap;
	}

	public void setLogMap(Map<String, Object> logMap) {
		this.logMap = logMap;
	}	
}
