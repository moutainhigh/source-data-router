package com.globalegrow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.globalegrow.bts.BtsPlanInfoRedisQuery;
import com.globalegrow.bts.model.BtsReport;
import com.globalegrow.bts.model.BtsZafulListPageReport;
import com.globalegrow.dy.bts.model.BtsZafulListPageColorReport;
import com.globalegrow.dy.report.DataTypeConvert;
import com.globalegrow.dy.report.enums.RecommendQuotaFields;
import com.globalegrow.dy.report.enums.ValueType;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTest {

    public final static List<String> LOG_FIELD_LIST= Arrays.asList(
            "glb_od",//cookie
            //"glb_u", //user_id
            "glb_b", //页面大类
            "geoip_country_name",//国家
            "stat_group_minutes",
            "glb_tm",//当前时间戳
            "glb_dc",//国家对应的编号
            "glb_plf",//记录不同平台的数据，区分PC和M版的数据
            "glb_mrlc",
            "is_cart",
            "is_order",
            "is_purchase",
            "is_pay_amount",
            "glb_sku",
            "glb_fmd",
            "order"
    );

    @Test
    public void test() throws Exception {
        String s = "{\n" +
                "\t\"glb_osr_referrer\": \"originalurl\",\n" +
                "\t\"glb_dc\": \"1301\",\n" +
                "\t\"glb_osr_landing\": \"https://m.zaful.com/\",\n" +
                "\t\"body_bytes_sent\": \"372\",\n" +
                "\t\"geoip_city_country_code\": \"US\",\n" +
                "\t\"glb_x\": \"\",\n" +
                "\t\"glb_w\": \"22131\",\n" +
                "\t\"glb_t\": \"ie\",\n" +
                "\t\"glb_u\": \"\",\n" +
                "\t\"glb_filter\": \"\",\n" +
                "\t\"glb_skuinfo\": \"\",\n" +
                "\t\"glb_s\": \"b01\",\n" +
                "\t\"http_user_agent\": \"Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1\",\n" +
                "\t\"http_true_client_ip\": \"162.228.78.51\",\n" +
                "\t\"http_accept_language\": \"en-us\",\n" +
                "\t\"glb_p\": \"39-1\",\n" +
                "\t\"remote_user\": \"-\",\n" +
                "\t\"glb_skuinfos\": \"\",\n" +
                "    \"is_pv\": 1,\n" +
                "    \"is_uv\": 1,\n" +
                "    \"is_click\": 1,\n" +
                "     \"is_exposure\": 1,\n" +
                "     \"is_cart\": 1,\n" +
                "    \"is_order\": 1,\n" +
                "    \"is_purchase\": 1,\n" +
                "    \"is_pay_amount\": 1,\n" +
                "\t\"glb_sc\": \"\",\n" +
                "     \"glb_sku\": \"\",\n" +
                "     \"glb_fmd\": \"\",\n" +
                "\t\"glb_osr\": \"\",\n" +
                "\t\"geoip_country_name\": \"United States\",\n" +
                "\t\"glb_od\": \"10013152931015578716vtnlu6304291\",\n" +
                "\t\"glb_ksku\": \"\",\n" +
                "\t\"glb_k\": \"sz01\",\n" +
                "\t\"glb_sl\": \"\",\n" +
                "\t\"glb_sk\": \"\",\n" +
                "\t\"glb_d\": \"10013\",\n" +
                "\t\"glb_mrlc\": \"\",\n" +
                "\t\"glb_b\": \"b\",\n" +
                "\t\"recommandExtField\": \"kylin recommand\",\n" +
                "\t\"glb_oi\": \"mq2r60s9aoo02vak925aqmcsm7\",\n" +
                "\t\"remote_addr\": \"72.31.49.96\",\n" +
                "\t\"glb_ubcta\": \"[{'sku': '253095103'}, {'sku': '212700802'}, {'sku': '257942601'}, {'sku': '269010504'}]\",\n" +
                "\t\"exposure_count\": 0,\n" +
                "\t\"glb_cl\": \"https://m.zaful.com/shorts-e_39/\",\n" +
                "\t\"time_local\": \"[19/Jun/2018:00:00:00 +0000]\",\n" +
                "\t\"glb_siws\": \"\",\n" +
                "\t\"glb_pagemodule\": \"\",\n" +
                "\t\"glb_olk\": \"\",\n" +
                "\t\"stat_group_minutes\": 1531457100720,\n" +
                "\t\"http_referer\": \"https://m.zaful.com/shorts-e_39/\",\n" +
                "\t\"glb_plf\": \"m\",\n" +
                "\t\"glb_tm\": \"1531456997720\",\n" +
                "\t\"http_x_forwarded_for\": \"s.logsss.com\",\n" +
                "\t\"glb_sckw\": \"\",\n" +
                "\t\"glb_pm\": \"mp\",\n" +
                "\t\"glb_pl\": \"https://m.zaful.com/blouses-e_19/\",\n" +
                "\t\"status\": \"200\",\n" +
                "\t\"order\":{\n" +
                "\t    \"id\":\"1\",\n" +
                "\t    \"status\":\"0\",\n" +
                "\t    \"amount\":0\n" +
                "\t}\n" +
                "}";

        Map<String, Object> map = JacksonUtil.readValue(s, Map.class);

        Map<String, Object> map1 = new HashMap<>();
        LOG_FIELD_LIST.stream().forEach(s1 -> map1.put(s1, map.get(s1)));
        System.out.println(JacksonUtil.toJSon(map1));
    }

    @Test
    public void testFloat() {
        String a = "1.0";
        if (a.indexOf(".") > 0) {
            System.out.println(a.substring(0, a.indexOf(".")));
        }

        //System.out.println(Integer.valueOf(a));
    }

    @Test
    public void testUbcta2() {
        String s = "{'versionid':'23', 'bucketid':'4', 'planid':'9', 'sku':'264183001', 'mrlc':'T_3'}";
        Map<String, String> map = GsonUtil.readValue(s, Map.class);
        System.out.println(map.get(""));

    }


    @Test
    public void testUbcta() throws Exception {
        String s = "[{'sku': '253095103'}, {'sku': '212700802'}, {'sku': '257942601'}, {'sku': '269010504'}]";
        List<Map<String, String>> mapList = JacksonUtil.readValue(s.replaceAll("'", "\""), new TypeReference<List<Map<String, String>>>() {});
        System.out.println(mapList.size());
        System.out.println(mapList.get(0).get("sku"));
    }

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();


    @Test
    public void testGson() {
        String s = "[{'sku': '253095103'}, {'sku': '212700802'}, {'sku': '257942601'}, {'sku': '269010504'}]";
        List<Map<String, String>> mapList = GsonUtil.readValue(s, List.class);
        System.out.println(mapList.size());
        System.out.println(mapList.get(0).get("sku"));
    }

    @Test
    public void testSimple() throws Exception {
        Map<String, Object> dataMap = GsonUtil.readValue("{\"glb_osr_referrer\":\"https://www.google.tt/\",\"is_cart\":0,\"glb_dc\":\"1301\",\"glb_sku\":\"\",\"body_bytes_sent\":\"372\",\"glb_x\":\"\",\"glb_w\":\"23094\",\"glb_t\":\"ie\",\"glb_u\":\"\",\"glb_s\":\"\",\"glb_p\":\"p-525955\",\"remote_user\":\"-\",\"glb_skuinfos\":\"\",\"is_collect\":0,\"glb_osr\":\"\",\"is_pay_amount\":0,\"glb_k\":\"sz01\",\"glb_d\":\"10013\",\"glb_b\":\"c\",\"is_exposure\":1,\"glb_ubcta\":[{\"versionid\":\"32\",\"bucketid\":\"6\",\"planid\":\"13\",\"sku\":\"243910101\",\"mrlc\":\"T_3\"},{\"versionid\":\"32\",\"bucketid\":\"6\",\"planid\":\"13\",\"sku\":\"262619002\",\"mrlc\":\"T_3\"},{\"versionid\":\"32\",\"bucketid\":\"6\",\"planid\":\"13\",\"sku\":\"270192001\",\"mrlc\":\"T_3\"},{\"versionid\":\"32\",\"bucketid\":\"6\",\"planid\":\"13\",\"sku\":\"271704301\",\"mrlc\":\"T_3\"},{\"versionid\":\"32\",\"bucketid\":\"6\",\"planid\":\"13\",\"sku\":\"268530301\",\"mrlc\":\"T_3\"}],\"glb_fmd\":\"\",\"exposure_count\":5,\"glb_cl\":\"https://www.zaful.com/snakeskin-print-bralette-bikini-set-p_525955.html\",\"time_local\":\"[06/Aug/2018:06:47:32 +0000]\",\"is_uv\":0,\"glb_pagemodule\":\"\",\"http_referer\":\"https://www.zaful.com/snakeskin-print-bralette-bikini-set-p_525955.html\",\"glb_plf\":\"pc\",\"glb_tm\":\"1533538052991\",\"is_click\":0,\"planid\":0,\"glb_pm\":\"mr\",\"glb_pl\":\"https://www.zaful.com/knotted-snakeskin-print-high-cut-bikini-set-p_523919.html\",\"status\":\"200\",\"glb_osr_landing\":\"https://www.zaful.com/knotted-snakeskin-print-high-cut-bikini-set-p_523919.html\",\"is_pv\":0,\"geoip_city_country_code\":\"TT\",\"is_order\":0,\"glb_filter\":\"\",\"glb_skuinfo\":\"\",\"http_user_agent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36\",\"http_true_client_ip\":\"170.84.9.6\",\"http_accept_language\":\"en-US,en;q=0.9\",\"glb_sc\":\"\",\"versionid\":0,\"geoip_country_name\":\"Trinidad and Tobago\",\"glb_od\":\"1001315334057314529q0r88v3869358\",\"glb_ksku\":\"260457701\",\"glb_sl\":\"\",\"glb_sk\":\"\",\"glb_mrlc\":\"T_3\",\"glb_oi\":\"qi2mhqjd1s3cbgi2tg9q0r88v3\",\"remote_addr\":\"172.31.49.32\",\"bucketid\":0,\"is_purchase\":0,\"glb_siws\":\"\",\"glb_olk\":\"14221702\",\"stat_group_minutes\":1533538800991,\"http_x_forwarded_for\":\"s.logsss.com\",\"glb_sckw\":\"\"}", Map.class);
        if (dataMap != null) {
            Map<String, String> bts = this.queryBtsInfo(dataMap);
            if (bts != null) {
                Map<String, Object> outJson = new HashMap<>();
                outJson.put("bts", bts);
                outJson.put(RecommendQuotaFields.specimen.recommendReportFields.name(), dataMap.get(RecommendQuotaFields.specimen.quota));
                for (RecommendQuotaFields reportFields : RecommendQuotaFields.values()) {
                    /*if (ValueType.value.equals(reportFields.valueType.name())) {
                        outJson.put(reportFields.recommendReportFields.name(), dataMap.get(reportFields.quota));
                    }*/
                    if (ValueType.num.name().equals(reportFields.valueType.name())) {

                        if (dataMap.get(reportFields.quota) != null && DataTypeConvert.calculatorType(dataMap, reportFields.quota) == 1) {
                            if ("is_exposure".equals(reportFields.quota)) {
                                outJson.put(reportFields.recommendReportFields.name(), dataMap.getOrDefault("exposure_count", 0));
                            }else {
                                outJson.put(reportFields.recommendReportFields.name(), 1);
                            }
                        }else {
                            outJson.put(reportFields.recommendReportFields.name(), 0);
                        }

                    }

                }
                outJson.put("timestamp", dataMap.get("glb_tm"));
               System.out.println(JacksonUtil.toJSon(outJson));
            }
        }
    }


    public Map<String, String> queryBtsInfo(Map<String, Object> dataMap) {
        Map<String, String> bts = null;
        Object ubcta = dataMap.get("glb_ubcta");
        if (ubcta != null) {
            if (ubcta instanceof Map) {
                bts = this.buildBtsMap((Map<String, String>) ubcta);
            } else if (ubcta instanceof List) {
                List<Map<String, String>> mapList = (List<Map<String, String>>) ubcta;
                if (mapList.size() > 0) {
                    bts = this.buildBtsMap(mapList.get(0));
                }
            } else if (ubcta instanceof String) {
                String ubctaStr = String.valueOf(ubcta);
                if (StringUtils.isNotEmpty(ubctaStr)) {
                    bts = this.getBtsInfoFromUbcta(ubctaStr);
                }
            }

        }

        return bts;
    }

    private Map<String, String> getBtsInfoFromUbcta(String ubcta) {
        if (ubcta.startsWith("[")) {
            List<Map<String, String>> mapList = GsonUtil.readValue(ubcta, List.class);
            if (mapList.size() > 0) {
                return this.buildBtsMap(mapList.get(0));
            }
        }
        if (ubcta.startsWith("{")) {
            return this.buildBtsMap(GsonUtil.readValue(ubcta, Map.class));
        }
        return null;
    }

    private Map<String, String> buildBtsMap(Map<String, String> ubMap) {
        String planid = ubMap.get(BtsPlanInfoRedisQuery.zafulBts.planid.name());
        String versionId = ubMap.get(BtsPlanInfoRedisQuery.zafulBts.versionid.name());
        String bucketid = ubMap.get(BtsPlanInfoRedisQuery.zafulBts.bucketid.name());
        if (StringUtils.isNotEmpty(planid) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketid)) {
            Map<String, String> btsMap = new HashMap<>();
            btsMap.put(BtsPlanInfoRedisQuery.bts.plan_id.name(), planid);
            btsMap.put(BtsPlanInfoRedisQuery.bts.version_id.name(), versionId);
            btsMap.put(BtsPlanInfoRedisQuery.bts.bucket_id.name(), bucketid);
            return btsMap;
        }
        return null;
    }

    @Test
    public void beanTest() throws Exception {
        BtsZafulListPageReport btsZafulListPageReport = new BtsZafulListPageReport();
        Map beanMap = new BeanMap(btsZafulListPageReport);
        Map<String, Object> map = new HashMap<>();
        if (MapUtils.isEmpty(beanMap)) {
            throw new Exception("实体类没有可用属性");
        } else {
            // 去除class属性
            for (Object key : beanMap.keySet()) {
                if (!"class".equals(key)) {
                    System.out.println(key);
                    map.put(String.valueOf(key), beanMap.get(key));
                }
            }
        }
        System.out.println(GsonUtil.toJson(map));
    }

    @Test
    public void colorReport() {
        BtsZafulListPageColorReport colorReport = new BtsZafulListPageColorReport();
        Map<String, String> bts = new HashMap<>();
        bts.put(BtsReport.btsFields.planid.name(), "");
        bts.put(BtsReport.btsFields.versionid.name(), "");
        bts.put(BtsReport.btsFields.bucketid.name(), "");
        bts.put(BtsReport.btsFields.plancode.name(), "");
        bts.put(BtsReport.btsFields.policy.name(), "");
        colorReport.setBts(bts);
        Map<String, Object> map = DyBeanUtils.objToMap(colorReport);
        map.put(NginxLogConvertUtil.TIMESTAMP_KEY, System.currentTimeMillis());
        System.out.println(GsonUtil.toJson(map));
    }

}
