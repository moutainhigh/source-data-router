package com.globalegrow;

import com.globalegrow.util.GsonUtil;
import org.junit.Test;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public class JsonTest {

    @Test
    public void test() {
        String json = "[{\"mrlc\":\"A_1\",\"sku\":\"266898001\",\"pdl\":\"1\"},{\"mrlc\":\"A_1\",\"sku\":\"273537502\",\"pdl\":\"1\"},{\"mrlc\":\"A_1\",\"sku\":\"227419402\",\"pdl\":\"1\"},{\"mrlc\":\"A_1\",\"sku\":\"189917201\",\"pdl\":\"1\"},{\"mrlc\":\"A_1\",\"sku\":\"218085601\"},{\"mrlc\":\"A_1\",\"sku\":\"273617801\"},{\"mrlc\":\"A_2\",\"sku\":\"270167101\"},{\"mrlc\":\"A_2\",\"sku\":\"239418201\"},{\"mrlc\":\"A_2\",\"sku\":\"276548301\"},{\"mrlc\":\"A_2\",\"sku\":\"274846201\"},{\"mrlc\":\"A_2\",\"sku\":\"228077701\"},{\"mrlc\":\"A_2\",\"sku\":\"210285903\"}]";
        List<Map<String, String>> list = GsonUtil.readValue(json, List.class);
        System.out.println(list.size());
        System.out.println(list);
    }

}
