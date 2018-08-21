package com.grobalegrow;

import com.globalegrow.util.JacksonUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.*;

public class test {

    @Test
    public void test2() throws Exception {
        String s = "{\"glb_osr_referrer\":\"originalurl\",\"is_cart\":0,\"glb_dc\":\"1301\",\"glb_sku\":\"\",\"body_bytes_sent\":\"372\",\"glb_x\":\"\",\"glb_w\":\"713\",\"glb_t\":\"ie\",\"glb_u\":\"\",\"glb_s\":\"b01\",\"glb_p\":\"28-1\",\"remote_user\":\"-\",\"glb_skuinfos\":\"\",\"glb_osr\":\"\",\"is_pay_amount\":0,\"glb_k\":\"sz01\",\"glb_d\":\"10013\",\"glb_b\":\"b\",\"is_exposure\":0,\"glb_ubcta\":[{\"sku\":\"228340201\"},{\"sku\":\"224238701\"},{\"sku\":\"232291101\"},{\"sku\":\"236097201\"},{\"sku\":\"218749103\"},{\"sku\":\"231879604\"},{\"sku\":\"201407601\"},{\"sku\":\"190719001\"}],\"glb_fmd\":\"\",\"exposure_count\":0,\"glb_cl\":\"http://www.zafulswimwear.com/earrings-e_28/\",\"time_local\":\"[30/Jul/2018:08:18:45 +0000]\",\"is_uv\":0,\"glb_pagemodule\":\"\",\"http_referer\":\"http://www.zafulswimwear.com/earrings-e_28/\",\"glb_plf\":\"pc\",\"glb_tm\":\"1532938725768\",\"is_click\":0,\"planid\":0,\"glb_pm\":\"mp\",\"glb_pl\":\"http://www.zafulswimwear.com/casual-dresses-e_50/\",\"status\":\"200\",\"glb_osr_landing\":\"http://www.zafulswimwear.com/earrings-e_28/\",\"is_pv\":0,\"geoip_city_country_code\":\"MX\",\"is_order\":0,\"glb_filter\":\"\",\"glb_skuinfo\":\"\",\"http_user_agent\":\"Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_1 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Version/11.0 Mobile/15C153 Safari/604.1\",\"http_true_client_ip\":\"177.228.168.35\",\"http_accept_language\":\"en-us\",\"glb_sc\":\"\",\"versionid\":0,\"geoip_country_name\":\"Mexico\",\"glb_od\":\"100131532937780132432480\",\"glb_ksku\":\"\",\"glb_sl\":\"\",\"glb_sk\":\"\",\"glb_mrlc\":\"\",\"glb_oi\":\"\",\"remote_addr\":\"172.31.29.237\",\"bucketid\":0,\"is_purchase\":0,\"glb_siws\":\"\",\"glb_olk\":\"\",\"stat_group_minutes\":1532939400768,\"http_x_forwarded_for\":\"s.logsss.com\",\"glb_sckw\":\"\"}";
        JacksonUtil.readValue(s, Map.class);
    }

    @Test
    public void test() throws Exception {
        String s = "{\"glb_osr_referrer\":\"https://go.oclasrv.com/afu.php\",\"glb_dc\":\"1301\",\"glb_osr_landing\":\"https://www.gearbest.com/promotion-electronics-top-stores-special-1865.html?lkid=12665561&cid=40847402961219584\",\"pv\":\"1\",\"body_bytes_sent\":\"372\",\"geoip_city_country_code\":\"PH\",\"glb_x\":\"\",\"glb_w\":\"125\",\"glb_t\":\"ie\",\"glb_u\":\"\",\"fdm\":\"\",\"glb_skuinfo\":\"\",\"glb_s\":\"b03\",\"http_user_agent\":\"\\\"Mozilla/5.0 (X11; U; Linux x86_64; en-us) AppleWebKit/537.36 (KHTML, like Gecko)  Chrome/30.0.1599.114 Safari/537.36 Puffin/4.8.0.2790AP\\\"\",\"http_true_client_ip\":\"107.178.44.201\",\"http_accept_language\":\"en-US,en;q=0.8\",\"glb_p\":\"1865\",\"remote_user\":\"-\",\"order_status\":\"\",\"geoip_country_name\":\"Philippines\",\"glb_od\":\"qxhmjyhsfpwr1531302272040\",\"glb_d\":\"10002\",\"sku\":\"\",\"glb_b\":\"b\",\"glb_oi\":\"70326a3b9c81e27c1322\",\"bts\":{\"recormand_type\":\"test\",\"creator\":\"1\",\"create_time\":\"2018-07-13 12:27:43\",\"cookie\":\"qxhmjyhsfpwr1531302272040\",\"product_line_code\":\"test\",\"plan_code\":\"test\",\"version_id\":\"4\",\"version_flag\":\"1\",\"plan_name\":\"test\",\"bucket_id\":\"1\",\"version_name\":\"test\",\"result_id\":\"2258\",\"plan_id\":\"2\",\"plan_status\":\"1\"},\"remote_addr\":\"172.31.29.237\",\"uv\":\"1\",\"glb_ubcta\":\"\",\"add_cart_num\":\"0\",\"exposure_count\":\"0\",\"glb_cl\":\"https://www.gearbest.com/promotion-electronics-top-stores-special-1865.html?lkid=12665561&cid=40847402961219584\",\"time_local\":\"[11/Jul/2018:09:44:58 +0000]\",\"pay_total\":\"0\",\"version_id\":\"4\",\"click_num\":\"0\",\"mrlc\":\"\",\"glb_olk\":\"12665561\",\"http_referer\":\"\\\"https://www.gearbest.com/promotion-electronics-top-stores-special-1865.html?lkid=12665561&cid=40847402961219584\\\"\",\"glb_plf\":\"pc\",\"glb_tm\":\"1531302298379\",\"http_x_forwarded_for\":\"s.logsss.com\",\"glb_pm\":\"\",\"glb_pl\":\"https://go.oclasrv.com/afu.php?zoneid=1407888&var=1621885\",\"order_id\":\"\",\"status\":\"200\"}";

        Map<String, String> map = JacksonUtil.readValue(s, Map.class);

        map.put("glb_tm", System.currentTimeMillis() + "");

        System.out.println(JacksonUtil.toJSon(map));

    }

    public static Map<String, String> EMPTY_ORDER = new HashMap() {{
        put("order_id", "_skip");
        put("order_status", "0");
        put("pay_total", "0");
        put("paid_order", "_skip");
    }};

    public static Map<String, String> EMPTY_BTS = new HashMap() {{
        put("bucket_id", "0");
        put("version_id", "0");
        put("plan_id", "0");
    }};

    List<Long> plans = Arrays.asList(new Long[]{1L, 2L});

    List<Long> versionIds = Arrays.asList(new Long[]{3L, 4L});



    @Test
    public void generateTestData() throws Exception {
        String source = "{\"bts\":{\"bucket_id\":\"1\",\"version_id\":\"3\",\"plan_id\":\"1\"},\"uv\":\"1\",\"glb_ubcta\":\"_skip\",\"pv\":\"1\",\"exposure_count\":\"0\",\"add_cart_num\":\"0\",\"glb_x\":\"ADT\",\"zaful_order\":{\"order_status\":\"1\",\"paid_order\":\"1\",\"pay_total\":\"1\",\"order_id\":\"1\"},\"click_num\":\"0\",\"glb_t\":\"ic\",\"glb_u\":\"2345465\",\"glb_skuinfo\":\"_skip\",\"mrlc\":\"_skip\",\"glb_od\":\"1001315292910982208rbfud82690258\",\"glb_plf\":\"pc\",\"glb_tm\":\"1531742468428\",\"glb_pm\":\"mp\",\"sku\":\"_skip\",\"fmd\":\"mr_T_1\",\"glb_b\":\"b\"}";
        Map<String, Object> map = JacksonUtil.readValue(source, Map.class);


        String planid = "7";
        String versionid = "8";
        String bucketid = "5";

        Map<String, String> bts = EMPTY_BTS;
        bts.put("bucket_id", bucketid);
        bts.put("version_id", versionid);
        bts.put("plan_id", planid);

        map.put("bts", bts);

        // Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        map.put("glb_tm", calendar.getTimeInMillis() + "");
        for (int i = 1; i < 10; i++) {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
            //System.out.println(DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
            for(int j = 0; j<2 ; j++){
                String cookie = UUID.randomUUID().toString();

                HashFunction hf = Hashing.murmur3_128();
                int hash =  hf.hashString(cookie, Charsets.UTF_8).asInt();
                if (hash < 0) {
                    hash = Math.abs(hash);
                }
                map.put("glb_od", cookie);
                if (hash % 3 == 0) {
                    map.put("glb_t", "ic");
                    if (hash % 2 ==0) {
                        map.put("click_num", "1");
                        map.put("add_cart_num", "1");
                        if (hash % 5 == 0) {
                            String orderId = UUID.randomUUID().toString();
                            Map<String, String> order = EMPTY_ORDER;
                            order.put("order_status", "1");
                            order.put("order_id", orderId);
                            order.put("paid_order", orderId);
                            order.put("pay_total", "20");
                            map.put("zaful_order", order);
                        }else {
                            String orderId = UUID.randomUUID().toString();
                            Map<String, String> order = EMPTY_ORDER;
                            order.put("order_status", "0");
                            order.put("order_id", orderId);
                            order.put("paid_order", "_skip");
                            order.put("pay_total", "0");
                            map.put("zaful_order", order);
                        }
                    }else {
                        map.put("click_num", "1");
                    }
                }else {
                    map.put("glb_t", "ie");
                    if (hash % 2 == 0) {
                        map.put("exposure_count", "1");
                    }
                }

                System.out.println(JacksonUtil.toJSon(map));

            }




        }

    }

}
