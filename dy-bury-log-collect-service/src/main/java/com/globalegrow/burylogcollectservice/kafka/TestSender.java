package com.globalegrow.burylogcollectservice.kafka;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
/**
 * 模拟日志生成器，把日志压入kafka
 * @author dengpizheng
 *
 */
@Component
public class TestSender {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    @Value("${test.logfile}")
    private String testLogLocation;
    @Value("${test.isopentestlog}")
    private Boolean isOpenTestLog;
    @Value("${kafka.source.data.topic}")
    private String sourceDataTopic;

    /**
     * 埋点数据发送到kafka
     * @throws Exception 
     */
    public void sendLog() throws Exception{
    	while(true && isOpenTestLog) {
    		BufferedReader br = new BufferedReader(new FileReader(this.testLogLocation));
        	while(br.read()!=-1) {
        		String log = br.readLine();
        		if(StringUtils.isNotBlank(log)) {
        			//每秒压两条
        			Thread.sleep(2);
        			this.kafkaTemplate.send(sourceDataTopic,log);
        		}        		
        	}
        	br.close();
    	}        
    }
}