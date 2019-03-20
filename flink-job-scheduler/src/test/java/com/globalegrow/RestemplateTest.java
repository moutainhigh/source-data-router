package com.globalegrow;

import com.globalegrow.util.JacksonUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RestemplateTest {


    @Test
    public void test() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
       // headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Content-Type", "application/json;charset=UTF-8");

        Map<String, String> map = new HashMap<>();
        map.put("platform", "0");
        map.put("productLineCode", "zaful");
        map.put("date", "2019-03-20");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platform", "pc");
        jsonObject.put("productLineCode", "zaful");
        jsonObject.put("date", "2019-03-20");

        HttpEntity<String> request = new HttpEntity<>(JacksonUtil.toJSon(map), headers);



        String response1 = restTemplate.postForObject("http://35.174.66.2:7087/targetConfig/sysn/notice", request, String.class);
        ResponseEntity<String> response = restTemplate.postForEntity("http://35.174.66.2:7087/targetConfig/sysn/notice", request, String.class);

        System.out.println(response);
    }

}
