package gw;

import com.globalegrow.burypointcollect.ServiceStart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceStart.class)
public class RestTemplateTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void  test() {
        //String utl = "http://52.202.56.79:17070/kylin/api/query";
        String utl = "http://3.211.250.110:7070/kylin/api/query";
        String base64up = new BASE64Encoder().encode(("ADMIN:KYLIN").getBytes());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.add("Authorization", "Basic " + base64up);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("project", "BTS_PROJECT_13");
        params.put("acceptPartial", true);
        params.put("sql", "select sum(pv) ,DAY_START,BTS_VERSION_ID,BTS_BUCKET_ID  \n" +
                "from BTS_STREAMING_TABLE_13 WHERE DAY_START between '2018-07-16' and '2018-07-16' \n" +
                "and BTS_BUCKET_ID = '1' and BTS_VERSION_ID = '3' \n" +
                "group by DAY_START,BTS_PLAN_ID,BTS_VERSION_ID,BTS_BUCKET_ID \n" +
                "order by DAY_START ");
        //params.put("buildType", "BUILD");
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, requestHeaders);
        ResponseEntity<String> stringResponseEntity = restTemplate.exchange(utl, HttpMethod.POST, requestEntity, String.class);
        System.out.println(stringResponseEntity.getBody());
    }

}
