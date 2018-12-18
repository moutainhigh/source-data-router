package com.globalegrow.bts.report;

import com.globalegrow.util.JacksonUtil;
import org.junit.Test;

import java.util.Map;

public class HiveTableCreateGenerate {

    String json = "{\"glb_osr_referrer\":\"originalurl\",\"glb_bts\":\"{\\\"versionid\\\":\\\"637\\\",\\\"bucketid\\\":\\\"55\\\",\\\"planid\\\":\\\"214\\\"}\",\"glb_dc\":\"1301\",\"year\":\"2018\",\"glb_osr_landing\":\"https://www.zaful.com/\",\"timestamp_hive\":1544670371,\"glb_x\":\"sku\",\"glb_w\":\"596293\",\"glb_t\":\"ic\",\"glb_u\":null,\"glb_filter\":null,\"glb_skuinfo\":\"{\\\"sku\\\":\\\"234462102\\\",\\\"pam\\\":\\\"0\\\",\\\"pc\\\":\\\"10\\\",\\\"k\\\":\\\"sz01\\\"}\",\"glb_s\":null,\"glb_p\":\"p-406694\",\"ymd\":\"2018-12-13\",\"seconds\":\"11\",\"glb_osr\":null,\"glb_od\":\"1001315445953061793ot0ljr2760999\",\"hour\":\"03\",\"glb_ksku\":\"231712404\",\"glb_k\":null,\"glb_sl\":null,\"glb_sk\":null,\"glb_d\":\"10013\",\"day\":\"13\",\"glb_b\":\"c\",\"glb_oi\":\"akv0ji4jkcdlh56kje3ot0ljr2\",\"log_id\":null,\"glb_ubcta\":\"{\\\"price\\\":\\\"25.99\\\",\\\"mrlc\\\":\\\"T_3\\\",\\\"sku\\\":\\\"234462102\\\",\\\"rank\\\":1,\\\"fmd\\\":\\\"mr_T_3\\\"}\",\"glb_cl\":\"https://www.zaful.com/button-up-denim-jacket-and-hooded-vest-p_406694.html\",\"recive_time\":1544670371193,\"minute\":\"06\",\"glb_pagemodule\":null,\"month\":\"12\",\"glb_device_id\":null,\"glb_plf\":\"m\",\"glb_tm\":\"1544613056055\",\"glb_sckw\":null,\"glb_pm\":\"mr\",\"glb_pl\":\"https://www.zaful.com/\"}";


    @Test
    public void testTableColums() throws Exception {
        Map<String, String> map = JacksonUtil.readValue(json, Map.class);
        map.keySet().forEach(s -> {System.out.println(s + " string ");});

    }

}
