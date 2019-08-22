package com.globalegrow.burylogcollectservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BuryPointLogHandler {
	@Autowired
	private InvokeDyKafkaService invokeDyKafkaService;

	@KafkaListener(topics = { "${kafka.source.data.topic}" })
	public void kafkaListener(String message) {
		// 这里必须异步多线程处理，
		this.invokeDyKafkaService.processLog(message);
	}
}
