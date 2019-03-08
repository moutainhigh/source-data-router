package com.globalegrow.text;

import com.globalegrow.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NginxLogTest {

    String pattern = "(.*?)=(.*?)&";
    String pattrenParameters = "_ubc.gif\\??(.*)HTTP";
    String patternTime = "\\^A\\^\\d{10}";
    String badUrlPattern = "%22sckw%22:%22[\\w\\W&\\/]+?%22";



    // String log = " 172.31.30.56^A^-^A^[26/Sep/2018:15:41:17 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_tm=1537976480589&glb_oi=7c5b04ad51588b5588dd4a49bea4467c&glb_d=10002&glb_b=b&glb_s=b01&glb_p=11267&glb_plf=pc&glb_dc=1301&glb_pm=mp&glb_x=sku&glb_ubcta={\\x22rank\\x22:\\x221\\x22,\\x22sckw\\x22:\\x22%2024%%20\\x22}&glb_skuinfo={\\x22sku\\x22:\\x22138430401\\x22,\\x22pam\\x22:0,\\x22pc\\x22:\\x2211267\\x22,\\x22k\\x22:\\x221433363\\x22}&glb_filter={\\x22view\\x22:\\x2236\\x22,\\x22sort\\x22:\\x22recommend\\x22,\\x22page\\x22:\\x221\\x22}&glb_w=82713&glb_olk=10731928&glb_od=100021523291665079bc6hpre1710446&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.gearbest.com%2Fled-strips-c_11267%2F%3Fpage%3D1%26testKey%3Dold%26attr%3D2234-10290&glb_cl=https%3A%2F%2Fwww.gearbest.com%2Fled-strips-c_11267%2F%3Fpage%3D1%26testKey%3Dold%26attr%3D2234-10290&glb_pl=https%3A%2F%2Fwww.gearbest.com%2Fled-strips-c_11267%2F HTTP/1.1\"^A^200^A^372^A^\"https://www.gearbest.com/led-strips-c_11267/?page=1&testKey=old&attr=2234-10290\"^A^\"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko\"^A^s.logsss.com^A^89.222.164.190, 10.21.240.50, 2.21.240.36, 10.20.245.117, 2.23.84.70^A^89.222.164.190^A^RU^A^Russian Federation^A^ru-RU^A^1537976477";
    String log = "172.31.25.191^A^-^A^[21/Nov/2018:04:06:44 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_tm=1542773204285&glb_oi=5c4e2a518781d34fefb1d3e3d5d7154c&glb_u=1510396&glb_d=10002&glb_b=b&glb_s=b02&glb_plf=pc&glb_dc=1301&glb_pm=mp&glb_x=sku&glb_ubcta={%22sk%22:%22%22,%22rank%22:%2234%22,%22sckw%22:%22Red%20LED%20Watch%20Black&Red%22}&glb_skuinfo={%22sku%22:%22281653102%22,%22pam%22:0,%22pc%22:%2211336%22,%22k%22:%221433363%22}&glb_filter={%22view%22:%2236%22,%22sort%22:%22relevance%22,%22page%22:%221%22}&glb_bts={%22versionid%22:%223%22,%22bucketid%22:%22%22,%22planid%22:%22%22,%22plancode%22:%22searchhot%22,%22policy%22:%22d%22}&glb_w=19687&glb_od=dnqhizwvsody1536428499983&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.gearbest.com%2Fsmartwatch-_gear%2F&glb_cl=https%3A%2F%2Fwww.gearbest.com%2FRed-LED-Watch-(Black%2526Red-_gear%2F&glb_pl=https%3A%2F%2Fwww.gearbest.com%2Fled-watches%2Fpp_269424.html%3Fwid%3D1433363 HTTP/1.1\"^A^200^A^372^A^\"https://www.gearbest.com/Red-LED-Watch-(Black%26Red-_gear/\"^A^\"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36\"^A^s.logsss.com^A^179.5.221.27, 10.201.103.218, 23.201.103.189, 10.203.174.28, 23.203.174.52^A^179.5.221.27^A^SV^A^El Salvador^A^es-419,es;q=0.9,en;q=0.8^A^1542773204";
    String log1 = "172.31.25.191^A^-^A^[21/Nov/2018:04:06:44 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_tm=1542773204285&glb_oi=5c4e2a518781d34fefb1d3e3d5d7154c&glb_u=1510396&glb_d=10002&glb_b=b&glb_s=b02&glb_plf=pc&glb_dc=1301&glb_pm=mp&glb_x=sku&glb_ubcta={%22sk%22:%22%22,%22rank%22:%2234%22,%22sckw%22:%22Red%20LED%20Watch%20BlackRed%22}&glb_skuinfo={%22sku%22:%22281653102%22,%22pam%22:0,%22pc%22:%2211336%22,%22k%22:%221433363%22}&glb_filter={%22view%22:%2236%22,%22sort%22:%22relevance%22,%22page%22:%221%22}&glb_bts={%22versionid%22:%223%22,%22bucketid%22:%22%22,%22planid%22:%22%22,%22plancode%22:%22searchhot%22,%22policy%22:%22d%22}&glb_w=19687&glb_od=dnqhizwvsody1536428499983&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.gearbest.com%2Fsmartwatch-_gear%2F&glb_cl=https%3A%2F%2Fwww.gearbest.com%2FRed-LED-Watch-(Black%2526Red-_gear%2F&glb_pl=https%3A%2F%2Fwww.gearbest.com%2Fled-watches%2Fpp_269424.html%3Fwid%3D1433363 HTTP/1.1\"^A^200^A^372^A^\"https://www.gearbest.com/Red-LED-Watch-(Black%26Red-_gear/\"^A^\"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36\"^A^s.logsss.com^A^179.5.221.27, 10.201.103.218, 23.201.103.189, 10.203.174.28, 23.203.174.52^A^179.5.221.27^A^SV^A^El Salvador^A^es-419,es;q=0.9,en;q=0.8^A^1542773204";
    String log2 = "172.31.25.191^A^-^A^[21/Nov/2018:04:06:44 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_tm=1542773204285&glb_oi=5c4e2a518781d34fefb1d3e3d5d7154c&glb_u=1510396&glb_d=10002&glb_b=b&glb_s=b02&glb_plf=pc&glb_dc=1301&glb_pm=mp&glb_x=sku&glb_ubcta={%22sk%22:%22%22,%22rank%22:%2234%22,%22sckw%22:%22Red%20LED%20Watch%20&Black&Red%22}&glb_skuinfo={%22sku%22:%22281653102%22,%22pam%22:0,%22pc%22:%2211336%22,%22k%22:%221433363%22}&glb_filter={%22view%22:%2236%22,%22sort%22:%22relevance%22,%22page%22:%221%22}&glb_bts={%22versionid%22:%223%22,%22bucketid%22:%22%22,%22planid%22:%22%22,%22plancode%22:%22searchhot%22,%22policy%22:%22d%22}&glb_w=19687&glb_od=dnqhizwvsody1536428499983&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.gearbest.com%2Fsmartwatch-_gear%2F&glb_cl=https%3A%2F%2Fwww.gearbest.com%2FRed-LED-Watch-(Black%2526Red-_gear%2F&glb_pl=https%3A%2F%2Fwww.gearbest.com%2Fled-watches%2Fpp_269424.html%3Fwid%3D1433363 HTTP/1.1\"^A^200^A^372^A^\"https://www.gearbest.com/Red-LED-Watch-(Black%26Red-_gear/\"^A^\"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36\"^A^s.logsss.com^A^179.5.221.27, 10.201.103.218, 23.201.103.189, 10.203.174.28, 23.203.174.52^A^179.5.221.27^A^SV^A^El Salvador^A^es-419,es;q=0.9,en;q=0.8^A^1542773204";
    String log3 = "172.31.25.191^A^-^A^[21/Nov/2018:20:16:02 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_w=16192&glb_tm=1542831363260&glb_sk=K&glb_siws=%7Bjjgj&glb_x=search&glb_plf=pc&glb_oi=r6tfer2ekdknb71v662iho16a3&glb_d=10007&glb_s=b02&glb_b=b&glb_p=s-kii&glb_dc=1301&glb_od=100071518309162131jm1nt1k0628916&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.rosegal.com%2F%3Futm_source%3Dhasoffers&glb_cl=https%3A%2F%2Fwww.rosegal.com%2Fkii%2Fshop%2F&glb_pl=https%3A%2F%2Fwww.rosegal.com%2Fu%2Fshop%2F&glb_sckw=%7Bjjgj&glb_ubcta=%7B%27at%27%3A0%7D HTTP/1.1\"^A^200^A^372^A^\"https://www.rosegal.com/%7Bjjgj/shop/\"^A^\"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36\"^A^s.logsss.com^A^189.234.54.93, 10.247.163.71, 189.247.163.85, 10.223.52.104, 173.223.52.109^A^189.234.54.93^A^MX^A^Mexico^A^es-ES,es;q=0.9^A^1542831362";

    String s = "https://s.logsss.com/_ubc.gif?glb_t=ie&glb_w=10836&glb_tm=1538027308072&glb_pm=md&glb_ubcta=[{%22price%22:%2216.78%22,%22mrlc%22:%22T_3%22,%22sku%22:%22263726502%22,%22rank%22:%221%22},{%22price%22:%2215.99%22,%22mrlc%22:%22T_3%22,%22sku%22:%22342979002%22,%22rank%22:%222%22},{%22price%22:%2216.84%22,%22mrlc%22:%22T_3%22,%22sku%22:%22288786602%22,%22rank%22:%223%22},{%22price%22:%2220.37%22,%22mrlc%22:%22T_3%22,%22sku%22:%22264558702%22,%22rank%22:%224%22},{%22price%22:%2220.31%22,%22mrlc%22:%22T_3%22,%22sku%22:%22342395302%22,%22rank%22:%225%22}]&glb_pl=https%3A%2F%2Fwww.zaful.com%2Factivewear-e_78%2F%3Fodr%3Dhot%26policy_key%3D1&glb_plf=pc&glb_bts=[{%22versionid%22:%221103%22,%22bucketid%22:%228%22,%22planid%22:%22366%22}]&glb_plf=pc&glb_oi=m5gi5dd08kg8iqrtpekmggbo46&glb_d=10013&glb_b=a&glb_k=sz01&glb_dc=1301&glb_od=100131527644242901364134&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.zaful.com%2F&glb_cl=https%3A%2F%2Fwww.zaful.com%2F HTTP/1.1\"^A^200^A^372^A^\"https://www.rosegal.com/%7Bjjgj/shop/\"^A^\"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36\"^A^s.logsss.com^A^189.234.54.93, 10.247.163.71, 189.247.163.85, 10.223.52.104, 173.223.52.109^A^189.234.54.93^A^MX^A^Mexico^A^es-ES,es;q=0.9^A^1542831362";

    @Test
    public void testJsonArray() throws Exception {
       int i = 0;
        while (i < 100){
            s = s + "  " + 1;
            i++;
            NginxLogConvertUtil.getNginxLogParameters(s);
        }
    }

    @Test
    public void testWhat() {
        String s = "{\"af_content_id\":\"280836502\",\"af_content_type\":\"product\",\"af_changed_size_or_color\":\"1\",\"af_inner_mediasource\":\"category_سترات خفيفة\",\"af_version_id\":\"\",\"af_bucket_id\":\"\",\"af_plan_id\":\"\",\"af_content_category\":\"\\/نساء\\/بلايز\\/سترات خفيفة\",\"af_price\":\"18.33\",\"af_quantity\":\"1\",\"af_currency\":\"USD\"}";
        System.out.println(s.split("&")[0]);
    }



    /**
     * %22sckw%22:%22Red%20LED%20Watch%20Black&Red%22
     */
    @Test
    public void testBadSckw() {
        long start = System.currentTimeMillis();
        Pattern p = Pattern.compile(badUrlPattern);
        Matcher m = p.matcher(log2);
        //String s = "";
        while (m.find()) {
            s = m.group();
            System.out.println(m.group());
        }
        System.out.println(System.currentTimeMillis() - start);
        //System.out.println(s.replaceAll("&", "%26"));
        /*if (m.find()) {
            System.out.println(m.group());
        }*/

    }
    @Test
    public void testLogToJson() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println(NginxLogConvertUtil.getNginxLogParameters(log));
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void test() throws Exception {
        String s = "{\"name\":\"Up%20to%2040%%20Off%20|%20Kinetic%20Sale\",\"type\":\"gbsite_custom\"}";
        JacksonUtil.readValue(s, Map.class);
    }

    @Test
    public void testprecent2() {
        String s = "{%22name%22:%22Ahorre%20hasta%20un%2060%%20de%20descuento%20|%20Los%20suministros%20de%20salud%20y%20belleza%22,%22type%22:%22gbsite_custom%22}";
        System.out.println(s.startsWith("{"));
        System.out.println(s.replaceAll("%22", "\"").replaceAll("%20", " "));
    }

    @Test
    public void appTest() {
        Map<String, Object> logMap = AppLogConvertUtil.getAppLogParameters(log);
        String eventValue = String.valueOf(logMap.get("event_value"));
        System.out.println(eventValue);
    }

    @Test
    public void utilTest() {
        //for (int i = 0; i < 10;i++) {
            long start = System.currentTimeMillis();
            //NginxLogConvertUtil.getNginxLogParameters(log);
            System.out.println("获取url参数耗时" + (System.currentTimeMillis() - start));
            System.out.println(NginxLogConvertUtil.getNginxLogParameters(log));
            //System.out.println(NginxLogConvertUtil.getNginxLogParameters(log));
        //}

    }


    @Test
    public void testUbc() throws UnsupportedEncodingException {
        String s = "{\\x22rank\\x22:\\x221\\x22,\\x22sckw\\x22:\\x22%2024%%20\\x22}";
        System.out.println(s.replaceAll("\\\\x22", "\"").replaceAll("\\x22", "\""));
        System.out.println(URLDecoder.decode("%2024%20", "utf-8"));
        System.out.println(URLDecoder.decode("%2024%%20", "utf-8"));
    }

    @Test
    public void timeTest() {
        Pattern p = Pattern.compile(patternTime);
        Matcher m = p.matcher(log);
        String requestStr = "";

        while (m.find()) {
            requestStr = m.group(0);
        }
        System.out.println(requestStr);
        String pattern = "\\d";
        Pattern p1 = Pattern.compile(pattern);
        Matcher m1 = p1.matcher(requestStr);
        StringBuilder time = new StringBuilder();
        while (m1.find()) {
            time.append(m1.group());
        }
        System.out.println(time.toString());
        System.out.println(new Date(Long.valueOf(time.toString()) * 1000));
    }

    @Test
    public void testHex() {
        System.out.println(GsonUtil.toJson(NginxLogConvertUtil.getNginxLogParameters(log)));
    }

    @Test
    public void testLogToMap() throws UnsupportedEncodingException {
        Pattern p = Pattern.compile(pattrenParameters);
        Matcher m = p.matcher(log);
        String requestStr = "";

        while (m.find()) {
            requestStr = m.group();
        }
        String decodedUrl = URLDecoder.decode(requestStr, "utf-8");
        System.out.println(decodedUrl);

        Map<String, Object> map = getUrlParams(requestStr);
        System.out.println(GsonUtil.toJson(map));
       /* String ubcta = (String) map.get("glb_ubcta");
        List<Map<String, String>> list = GsonUtil.readValue(ubcta, List.class);
        System.out.println(list.size());
        System.out.println(list.get(0).get("sku"));*/

        String glbS = String.valueOf(map.get("glb_s"));
        String glbPlf = String.valueOf(map.get("glb_plf"));
        String glbX = String.valueOf(map.get("glb_x"));
        String glbOd = String.valueOf(map.get("glb_od"));
        String glbCl = String.valueOf(map.get("glb_cl"));
        System.out.println(glbCl);
        System.out.println(("f01".equals(glbS) && "pc".equals(glbPlf) && "SIGNIN".equals(glbX)
                && "https://login.rosegal.com/m-users-a-sign.htm?flow=checkout".equals(glbCl)));
    }

    public static Map<String, Object> getUrlParams(String param) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            System.out.println(p[0] + " " + p[1]);
            if (p.length == 2) {
                String key = p[0];
                if (key.startsWith("_ubc.gif?")) {
                    map.put(key.replace("_ubc.gif?", ""), URLDecoder.decode(p[1], "utf-8"));
                }else {
                    map.put(p[0], URLDecoder.decode(p[1], "utf-8"));
                }
            }
        }
        return map;
    }


}
