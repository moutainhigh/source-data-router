package gw;
import com.globalegrow.dyCubeBuildService.ServiceStart;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * redis 单元测试类
 * @author dengpizheng
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceStart.class)
public class Test {
	@Autowired
	private  RestTemplate restTemplate;
	@org.junit.Test
	public void test() throws Exception {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.add("Authorization", "Basic QURNSU46S1lMSU4=");
       
       
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("sourceOffsetStart", 0);
        params.put("sourceOffsetEnd", 9223372036854775807L);
        params.put("buildType", "BUILD");
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<Map<String,Object>>(params, requestHeaders);
        for(int i = 0 ;i < 100 ;i++){
        	if(i != 0) {
        		Thread.sleep(300000);  
        	}
        	
        	System.out.println("begin:"+i);
        	try {
        		//restTemplate.exchange("http://52.203.246.147:7070/kylin/api/cubes/kafka_cude/build2", HttpMethod.PUT, requestEntity, String.class);
        		restTemplate.exchange("http://3.211.250.110:7070/kylin/api/cubes/kafka_cude/build2", HttpMethod.PUT, requestEntity, String.class);
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        	System.out.println("end:"+i);
        }
		//System.out.println(SecurityUtils.encodeBase64("ADMIN:KYLIN"));
	}

}
