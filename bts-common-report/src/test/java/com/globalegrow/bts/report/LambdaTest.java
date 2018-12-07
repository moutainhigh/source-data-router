package com.globalegrow.bts.report;

import com.globalegrow.util.JacksonUtil;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaTest {

    @Test
    public void test() throws Exception {
        String json = "{\n" +
                "    \"ip\": [\n" +
                "        \"10.16.13.107\",\n" +
                "        \"10.16.13.108\",\n" +
                "        \"10.16.13.109\"\n" +
                "    ],\n" +
                "    \"port\": 8090\n" +
                "}";

        Map<String, Object> map = JacksonUtil.readValue(json, Map.class);

        //Stream<Map.Entry<String, Object>> stream = map.entrySet().stream();

        map.entrySet().stream().map(e -> {
            return ((List<String>) map.get("ip")).stream().map(s -> s + ":" + map.get("port")).collect(Collectors.toList());
        });

        List<String> ipportss = ((List<String>) map.get("ip")).stream().map(s -> s + ":" + map.get("port")).collect(Collectors.toList());

        System.out.println(JacksonUtil.toJSon(ipportss));

    }

}
