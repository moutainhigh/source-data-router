package com.globalegrow.burylogcollectservice.kafka;

import com.alibaba.fastjson.JSONObject;
import com.globalegrow.burylogcollectservice.service.IBuryPointService;
import com.globalegrow.burylogcollectservice.utils.SpringContextUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Component("invokeDyKafkaService")
@ConfigurationProperties("extdata")
public class InvokeDyKafkaService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Value("${dy.kafka.service.url}")
	private String dyKafkaServiceUrl;
	@Autowired
	private IBuryPointService buryPointService;
	@Value("${kafka.default.topic}")
	private String defaultTopic;	
	/**
	 * 从配置文件里面定义的数组extdata.invokeinfo获取值
	 */
	private List<String> invokeinfo = new ArrayList<String>();
	
	@Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
	
	/**
	 * 读消息
	 * 
	 * @param message
	 * @throws Exception
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */	
	@Async
	public void processLog(String logLine) {
		try {
			// 解析埋点，已json格式放到大禹项目的kafka
			if (StringUtils.isNoneBlank(logLine)) {
				HashMap<String, Object> logMap = this.buryPointService.logToMap(logLine);
				if (MapUtils.isNotEmpty(logMap)) {
					// 调用需要扩展字段的方法，各主题开发人员自己定制
					if (CollectionUtils.isNotEmpty(invokeinfo)) {
						this.invokeExtMethodAndSendToKafka(logMap);
					} else {// 如果没有配置需要扩展字段，日志压到默认topic
						List<String> topics = new ArrayList<String>();
						topics.add(this.defaultTopic);
						this.sendToKafka(logMap, topics);
					}
				}
			}
		} catch (Exception e) {
			logger.error("BuryPointLogHandler.processLog exception", e);
		}

	}	
	
	private void sendToKafka(Map<String, Object> logMap, List<String> topics) {	
		String message = JSONObject.toJSONString(logMap);
		if(CollectionUtils.isNotEmpty(topics) || StringUtils.isNotBlank(message)) {
			for(String topic:topics) {
				this.kafkaTemplate.send(topic,message);
			}
			logger.info("send to dy kafak success.topics:{}:message:{}",topics,message);
		}
	}

	/**
	 * 根据配置调用扩展字段方法并发送kafka
	 * 
	 * @param logMap
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void invokeExtMethodAndSendToKafka(HashMap<String, Object> logMap)
			throws IllegalAccessException, InvocationTargetException {
		for (String str : invokeinfo) {
			@SuppressWarnings("unchecked")
			Map<String, Object> sourceMap = (Map<String, Object>) logMap.clone();
			String[] invokeInfos = str.split(":");
			Object serviceObj = SpringContextUtils.getBean(invokeInfos[0]);

			Method[] methods = serviceObj.getClass().getMethods();
			if (ArrayUtils.isNotEmpty(methods)) {
				Method invokeMethod = this.findMethod(invokeInfos[1], methods);
				invokeMethod.invoke(serviceObj, sourceMap);
				String topics = invokeInfos[2];
				String isSend = invokeInfos[3];
				if (StringUtils.isBlank(topics)) {
					topics = defaultTopic;
				}
				String[] topicArr = topics.split("\\|");
				if (MapUtils.isNotEmpty(sourceMap)&&"yes".equals(isSend)) {
					this.sendToKafka(sourceMap, Arrays.asList(topicArr));
				}
			}
		}
	}

	

	/**
	 * 找到被调用的方法
	 * 
	 * @param func
	 * @param methods
	 * @return
	 */
	private Method findMethod(String func, Method[] methods) {
		Method invokeMethod = null;
		for (Method method : methods) {
			if (func.equals(method.getName())) {
				invokeMethod = method;
				break;
			}
		}
		return invokeMethod;
	}

	public List<String> getInvokeinfo() {
		return invokeinfo;
	}

	public void setInvokeinfo(List<String> invokeinfo) {
		this.invokeinfo = invokeinfo;
	}
}
