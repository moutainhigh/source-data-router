package com.globalegrow.bts.report;

import com.globalegrow.util.JacksonUtil;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class LongTest {

    @Test
    public void test() {
        Float f = 1495436040.177F * 1000;
        System.out.println(f.longValue());
    }

    @Test
    public void testJson() throws Exception {
        String s = "{\"uri_args\":{\"passwd\":\"123456\",\"id\":\"1\",\"json_list\":\"[{\\\"cpID\\\":\\\"50872\\\",\\\"cpnum\\\":\\\"U000177\\\",\\\"cplocation\\\":\\\"1\\\",\\\"sku\\\":\\\"276794802\\\",\\\"cporder\\\":\\\"10\\\",\\\"rank\\\":\\\"0\\\"},{\\\"cpID\\\":\\\"50872\\\",\\\"cpnum\\\":\\\"U000177\\\",\\\"cplocation\\\":\\\"1\\\",\\\"sku\\\":\\\"234462102\\\",\\\"cporder\\\":\\\"10\\\",\\\"rank\\\":\\\"1\\\"},{\\\"cpID\\\":\\\"50872\\\",\\\"cpnum\\\":\\\"U000177\\\",\\\"cplocation\\\":\\\"1\\\",\\\"sku\\\":\\\"291513605\\\",\\\"cporder\\\":\\\"10\\\",\\\"rank\\\":\\\"2\\\"}]\",\"username\":\"sdfwereriouwoir\",\"jsonMap\":\"{\\\"view\\\":null,\\\"sort\\\":\\\"hot\\\",\\\"page\\\":{\\\"page_name\\\":\\\"page\\\",\\\"next_page\\\":\\\"Next»\\\",\\\"pre_page\\\":\\\"«Prev\\\",\\\"first_page\\\":\\\"«\\\",\\\"last_page\\\":\\\"»\\\",\\\"pre_bar\\\":\\\"<<\\\",\\\"next_bar\\\":\\\">>\\\",\\\"format_left\\\":\\\" \\\",\\\"format_right\\\":\\\" \\\",\\\"is_ajax\\\":false,\\\"pagebarnum\\\":8,\\\"totalpage\\\":102,\\\"ajax_action_name\\\":\\\"\\\",\\\"nowindex\\\":1,\\\"url\\\":\\\"\\/deals\\/\\\",\\\"offset\\\":0,\\\"perpage\\\":120,\\\"creat\\\":0,\\\"total\\\":12238}}\"},\"recive_time\":1544498074456}";

        Map<String, Object> map = JacksonUtil.readValue(s, Map.class);

        Map<String, Object> uri = (Map<String, Object>) map.get("uri_args");

        System.out.println(uri.get("json_list").getClass());
        System.out.println(uri.get("jsonMap").getClass());

        String jsonList = String.valueOf(uri.get("json_list"));

        List list = JacksonUtil.readValue(jsonList, List.class);

        list.forEach(o -> System.out.println(o.getClass()));


    }

}
