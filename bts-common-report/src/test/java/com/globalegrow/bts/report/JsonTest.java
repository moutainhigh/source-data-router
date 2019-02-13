package com.globalegrow.bts.report;

import com.globalegrow.report.order.ReportOrderInfo;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTest {

    public static final String BAD_JSON_PATTERN = "\"([\\d]+_?)\":";

    String s = "https://s.logsss.com/_ubc.gif?glb_t=ie&glb_w=10836&glb_tm=1538027308072&glb_pm=md&glb_ubcta=[{%22mrlc%22:%22B_3%22,%22mdID%22:%22812%22}]&glb_plf=pc&glb_oi=m5gi5dd08kg8iqrtpekmggbo46&glb_d=10013&glb_b=a&glb_k=sz01&glb_dc=1301&glb_od=100131527644242901364134&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.zaful.com%2F&glb_cl=https%3A%2F%2Fwww.zaful.com%2F HTTP/1.1\"^A^200^A^372^A^\"https://www.rosegal.com/%7Bjjgj/shop/\"^A^\"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36\"^A^s.logsss.com^A^189.234.54.93, 10.247.163.71, 189.247.163.85, 10.223.52.104, 173.223.52.109^A^189.234.54.93^A^MX^A^Mexico^A^es-ES,es;q=0.9^A^1542831362";

    @Test
    public void testUbctaFilter() throws Exception {
        Map<String, Object> map = NginxLogConvertUtil.getNginxLogParameters(s);
        System.out.println(map);
        String ubcta = (String) map.get("glb_ubcta");
        List<Map> mapList = JacksonUtil.readValue(ubcta, List.class);
        System.out.println(mapList);
        map.put("glb_ubcta", mapList);
        String json = JacksonUtil.toJSon(map);
        ReadContext ctx  = JsonPath.parse(json);
        System.out.println(ctx.read("$.glb_ubcta[0].mrlc", String.class));

    }

    @Test
    public void testUUID() {
        System.out.println(UUID.randomUUID().toString());
    }

    @Test
    public void testList(){
        ArrayList arrayList = new ArrayList();
        System.out.println(arrayList instanceof List);
    }

    @Test
    public void testReportJson() throws Exception {
        ReportOrderInfo reportOrderInfo = new ReportOrderInfo();
        System.out.println(JacksonUtil.toJSon(reportOrderInfo));
    }

    @Test
    public void testNUllMap() throws Exception {
        String s = "{}";
        JacksonUtil.readValue(s, Map.class);
    }

    @Test
    public void testJsonMap() throws Exception {
        String s = "{\"fmd\":\"mp\",\"sk\":\"\",\"sc\":\"All Categories \",\"sckw\":\"277225301\",\"k\":\"1433363\"}";
        JacksonUtil.readValue(s, Map.class);
    }



    @Test
    public void testDecoder() throws Exception {
        long start = System.currentTimeMillis();
        String s = "{\\x22name\\x22:\\x22\\xD0\\xA3\\xD0\\xB2\\xD0\\xB5\\xD0\\xB4\\xD0\\xBE\\xD0\\xBC\\xD0\\xBB\\xD0\\xB5\\xD0\\xBD\\xD0\\xB8\\xD0\\xB5%20\\xD0\\xBE\\xD0\\xB1%20\\xD0\\xB8\\xD1\\x81\\xD1\\x82\\xD0\\xB5\\xD1\\x87\\xD0\\xB5\\xD0\\xBD\\xD0\\xB8\\xD0\\xB8%20\\xD1\\x81\\xD1\\x80\\xD0\\xBE\\xD0\\xBA\\xD0\\xB0%20\\xD0\\xBA\\xD1\\x83\\xD0\\xBF\\xD0\\xBE\\xD0\\xBD\\xD0\\xB0\\x22,\\x22type\\x22:\\x22coupon\\x22}";
        //JacksonUtil.readValue(s.replaceAll("\\\\x", ""), Map.class);
        System.out.println( URLDecoder.decode(s.replaceAll("\\\\x","%"),"UTF-8") );
        String[] strings = s.split(":");
        StringBuffer stringBuilder = new StringBuffer();
        Arrays.asList(strings).parallelStream().forEach(value -> {
            System.out.println(MyLogStringUtils.unescape_perl_string( value));
        });
        /*Arrays.stream(strings).forEach(value -> {
            System.out.println(MyLogStringUtils.unescape_perl_string(value));
        });*/
        System.out.println("并行处理耗时：" + (System.currentTimeMillis() - start));
        String Utf8 = new String(s.getBytes(), "utf-8");
        System.out.println(Utf8);
        System.out.println(URLDecoder.decode(s, "utf-8"));
        System.out.println(UrlUtils.urlDecode(s));
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(MyLogStringUtils.unescape_perl_string(s));
        System.out.println(System.currentTimeMillis() - start);
        JacksonUtil.readValue(MyLogStringUtils.unescape_perl_string(s), Map.class);
        System.out.println(System.currentTimeMillis() - start);
    }

    public static String str2Hex(String str) throws UnsupportedEncodingException {
        String hexRaw = String.format("%x", new BigInteger(1, str.getBytes("UTF-8")));
        char[] hexRawArr = hexRaw.toCharArray();
        StringBuilder hexFmtStr = new StringBuilder();
        final String SEP = "\\x";
        for (int i = 0; i < hexRawArr.length; i++) {
            hexFmtStr.append(SEP).append(hexRawArr[i]).append(hexRawArr[++i]);
        }
        return hexFmtStr.toString();
    }

    public static String hex2Str(String str) throws UnsupportedEncodingException {
        String strArr[] = str.split("\\\\"); // 分割拿到形如 xE9 的16进制数据
        System.out.println(strArr);
        byte[] byteArr = new byte[strArr.length - 1];
        for (int i = 1; i < strArr.length; i++) {
            Integer hexInt = Integer.decode("0" + strArr[i]);
            byteArr[i - 1] = hexInt.byteValue();
        }

        return new String(byteArr, "UTF-8");
    }

    @Test
    public void testMapJdon() throws Exception {
        String ss1 = "{'at'";
        String s = " {\"211057101\":{\"amount\":\"0\",\"category\":\"11294\"}}";
        System.out.println(s.contains("\\xE9t"));
        System.out.println(s.replaceAll("x5C", ""));
        Map map = JacksonUtil.readValue(s.replaceAll("xE9t", ""), Map.class);

    }

    @Test
    public void testJsonList() throws Exception {
        String s = "[{\"cpID\":\"26947\",\"cpnum\":\"U000071\",\"cplocation\":\"1\",\"mdID\":\"https://uidesign.zafcdn.com/ZF/image/banner/20181103_5785/pc_a.jpg?impolicy=true\",\"cporder\":\"3\",\"rank\":\"1\"},{\"cpID\":\"26948\",\"cpnum\":\"U000071\",\"cplocation\":\"2\",\"mdID\":\"https://uidesign.zafcdn.com/ZF/image/banner/20181103_5785/pc_b.jpg?impolicy=true\",\"cporder\":\"3\",\"rank\":\"1\"},{\"cpID\":\"17293\",\"cpnum\":\"U000058\",\"cplocation\":\"1\",\"mdID\":\"https://uidesign.zafcdn.com/ZF/image/banner/20181103_5786/pc_1111_en_1240.jpg\",\"cporder\":\"1\",\"rank\":\"1\"}]";
        List list = JacksonUtil.readValue(s, List.class);
        System.out.println(list.size());
    }

    @Test
    public void TestJson() throws Exception {
        String s = "[\"'xiaomi mi box'\",\"'xiaomi mi box'\",\"'xiaomi mi box'\"]";
        List list = JacksonUtil.readValue(s.replaceAll("HTTP\"}", "").replaceAll("'", ""), List.class);
        System.out.println(list.size());
    }

    @Test
    public void jsonPathTest() throws Exception {
        String json = "{\"glb_osr_referrer\":\"https://www.bing.com/search\",\"glb_dc\":\"1301\"," +
                "\"utm_campaign\":\"[ETA]_Brand_thrOther_NativeLanguage_phrase\",\"utm_medium\":\"cpc\",\"glb_cl\":\"https://www.rosegal.com/vintage-dresses/vintage-polka-dot-pin-up-dress-1882091.html\"" +
                ",\"glb_w\":\"4305\",\"glb_t\":\"ie\"" +
                ",\"glb_skuinfo\":\"{\\\"sku\\\":\\\"251362407\\\",\\\"pam\\\":1,\\\"pc\\\":\\\"1\\\",\\\"k\\\":\\\"sz01\\\"}\",\"glb_p\":\"x-251362407\",\"utm_term\":\"rosegal\"" +
                ",\"glb_od\":\"100071541323929939tjafuli1181882\",\"glb_plf\":\"pc\",\"glb_tm\":\"1541925390571\",\"glb_d\":\"10007\",\"glb_pl\":\"https://www.rosegal.com/vintage-dresses-1/2.html HTTP\",\"glb_b\":\"c\",\"glb_oi\":\"ibeeoj5fnqtfpd4d63f7t0m933\",\"utm_source\":\"bing\",\"utm_content\":\"[ETA]_Brand_thrOther_NativeLanguage_phrase_FR\",\"timestamp\":1541925380000}";
        ReadContext ctx = JsonPath.parse(json);
        System.out.println(ctx.read("$.glb_skuinfo", Map.class));
        Map<String, Object> map = JacksonUtil.readValue(json, Map.class);
        map.put("glb_skuinfo", JacksonUtil.readValue(String.valueOf(map.get("glb_skuinfo")), Map.class));
        String s1 = JacksonUtil.toJSon(map);
        ReadContext ctx2 = JsonPath.parse(s1);
        System.out.println(ctx2.read("$.glb_skuinfo.sku", Integer.class));

    }

    @Test
    public void testJson() throws Exception {
        String json = " {\"glb_bts\":{\"versionid\":\"0\",\"bucketid\":\",\"planid\":\",\"plancode\":\"searchhot\",\"policy\":\"a\"},\"glb_dc\":\"1301\",\"glb_ubcta\":[{\"sku\":\"203277601\"},{\"sku\":\"229971201\"},{\"sku\":\"235085601\"},{\"sku\":\"342794401\"},{\"sku\":\"337093125\"},{\"sku\":\"338356412\"},{\"sku\":\"337095706\"},{\"sku\":\"307615503\"},{\"sku\":\"308403110\"}],\"glb_w\":\"19245\",\"glb_t\":\"ie\",\"glb_s\":\"b01\",\"glb_p\":\"12596\",\"glb_olk\":\"17193534\",\"glb_od\":\"cjuzgkozkvvz1541924382311\",\"glb_plf\":\"pc\",\"glb_tm\":\"1541924400017\",\"glb_d\":\"10002\",\"glb_pm\":\"mp\",\"glb_b\":\"b\",\"glb_oi\":\"853cc9d68157d2fedab31b126e7305f4\",\"timestamp\":1541924401000}";

        json = json.replaceAll("\"\\{", "{").replaceAll("}\"", "}")
                .replaceAll("\"\\[", "[").replaceAll("]\"", "]").replaceAll(" ", "")
                .replaceAll("\\\\", "").replaceAll(" ", "").replaceAll("\"\"\"\"", "\"").replaceAll("\"\"\"", "\"");
        System.out.println(json);
        Map<String, Object> map = JacksonUtil.readValue(json, Map.class);
        System.out.println(map.get("glb_skuinfo").getClass());
    }

    //public static final String BAD_JSON_PATTERN = "\"([\\d]+_?)\":";
    // {"sku":"269433201","pam""1":,"pc":"11351","k":"1433363","price""22":.89}
    public static final String BAD_JSON_PATTERN2 = "\"([\\d]+_?)\":.([\\d]+_?)";
    public static final String BAD_JSON_QUOTE_PATTERN = "\"'";

    @Test
    public void testBadNumber() {
        String json = "{\"sku\":\"269433201\",\"pam\"\"1\":,\"pc\":\"11351\",\"k\":\"1433363\",\"price\"\"22\":.89}";
        String badJson = json;
        Pattern p = Pattern.compile(BAD_JSON_PATTERN2);
        Matcher m = p.matcher(badJson);
        List<String> key = new ArrayList<>();
        while (m.find()) {
            String badName = m.group();
            key.add(badName);
        }
        System.out.println(key);
        for (String badName : key) {
            badJson = badJson.replaceAll(badName, ":" + badName.split(":")[0].replaceAll("\"", "") + badName.split(":")[1]);
        }
        System.out.println(badJson);
        System.out.println(NginxLogConvertUtil.handleBadJson(badJson));
    }

    public static final String BAD_QUOTE_PATTERN = "([\"\"]){2,10}";

    public static final String BAD_QUOTE_END_PATTERN = "([\",]){3,10}";

    public static final String BAD_MAP_JSON_PATTERN = "\"glb_ubcta\":\"\\{[\\w]\\}\"";

    @Test
    public void test1() {
        String log = "{\"glb_osr_referrer\":\"originalurl\",\"glb_dc\":\"1301\",\"glb_ubcta\":\"{\\\"at\\\":\\\"1504\\\"}}\",\"glb_osr_landing\":\"https://m.zaful.com/\"" +
                ",\"glb_x\":\"search\",\"glb_cl\":\"https://m.zaful.com/half-zip-plain-faux-fur-sweatshirt-p_593930.html\"" +
                ",\"glb_w\":\"39669\",\"glb_t\":\"ic\",\"glb_p\":\"p-593930\",\"glb_siws\":\"ja\",\"glb_od\":\"100131537721796215kh8otvh7459891\"" +
                ",\"glb_ksku\":\"291513605\",\"glb_plf\":\"m\",\"glb_k\":\"sz01\",\"glb_tm\":\"1541925377763\",\"glb_sl\":\"1\",\"glb_sk\":\"L\"" +
                ",\"glb_sckw\":\"jacketjean jacketjumperjacketjewelryjoggerjeanjacketjacket for men\"" +
                ",\"glb_d\":\"10013\",\"glb_b\":\"c\",\"glb_oi\":\"nk5n7vjlmr4dcrcd4k8mdtc5u1\",\"timestamp\":1541925379000}";
        Pattern p = Pattern.compile(BAD_MAP_JSON_PATTERN);
        Matcher m = p.matcher(log);
        StringBuilder stringBuilder = new StringBuilder();
        while (m.find()) {
            stringBuilder.append(m.group());
        }
        System.out.println(stringBuilder.toString());
    }


    @Test
    public void badJson2() {
        String json = "{\"glb_osr_referrer\":\"originalurl\",\"glb_dc\":\"1301\",\"glb_ubcta\":{\"sckw\":\"hoodedjacket\",\"rank\"\"\"84\"\":,\"fmd\":\"mp\"},\"glb_osr_landing\":\"https://www.zaful.com/w/hooded-jacket/e_1/\",\"glb_x\":\"ADF\",\"glb_cl\":\"https://www.zaful.com/w/hooded-jacket/e_1/\",\"glb_w\":\"92615\",\"glb_t\":\"ic\",\"glb_filter\":{\"view\"\"\"120\"\":,\"sort\":\"recommend\",\"page\"\"\"1\"\":},\"glb_skuinfo\":{\"sku\":\"281031602\",\"price\":\"39.99\",\"discount_mark\":\"47OFF\",\"pam\":\"0\",\"pc\":\"8\"},\"glb_s\":\"b02\",\"glb_od\":\"100131541923157450f5ced385238078\",\"glb_plf\":\"pc\",\"glb_tm\":\"1541924131165\",\"glb_pm\":\"mp\",\"glb_d\":\"10013\",\"glb_pl\":\"https://www.zaful.com/s/hooded-jacket/HTTP\",\"glb_b\":\"b\",\"glb_oi\":\"lmqk71nrg660rso7fnf5ced385\",\"timestamp\":1541924129000}";
        String badJson = json;
        Pattern p = Pattern.compile(BAD_QUOTE_PATTERN);
        Matcher m = p.matcher(badJson);
        List<String> key = new ArrayList<>();
        while (m.find()) {
            String badName = m.group();
            key.add(badName);
        }
        System.out.println(key);
        for (String badName : key) {
            badJson = badJson.replaceAll(badName, "\"");
        }
        System.out.println(badJson);

    }

    @Test
    public void badJson() {
        String source = "{\"view\"\"36\":,\"sort\":\"Correspond\\xC3\\xAAncia Perfeita\",\"page\"\"1\":}";
        String badJson = source;
        Pattern p = Pattern.compile(BAD_JSON_PATTERN);
        Matcher m = p.matcher(badJson);
        List<String> key = new ArrayList<>();
        while (m.find()) {
            String badName = m.group();
            key.add(badName);
        }
        System.out.println(key);
        for (String badName : key) {
            badJson = badJson.replaceAll(badName, ":" + badName.replaceAll(":", ""));
        }
        System.out.println(badJson);

    }

}
