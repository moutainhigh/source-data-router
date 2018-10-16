package com.globalegrow.text;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

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
    // String log = " 172.31.30.56^A^-^A^[26/Sep/2018:15:41:17 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_tm=1537976480589&glb_oi=7c5b04ad51588b5588dd4a49bea4467c&glb_d=10002&glb_b=b&glb_s=b01&glb_p=11267&glb_plf=pc&glb_dc=1301&glb_pm=mp&glb_x=sku&glb_ubcta={\\x22rank\\x22:\\x221\\x22,\\x22sckw\\x22:\\x22%2024%%20\\x22}&glb_skuinfo={\\x22sku\\x22:\\x22138430401\\x22,\\x22pam\\x22:0,\\x22pc\\x22:\\x2211267\\x22,\\x22k\\x22:\\x221433363\\x22}&glb_filter={\\x22view\\x22:\\x2236\\x22,\\x22sort\\x22:\\x22recommend\\x22,\\x22page\\x22:\\x221\\x22}&glb_w=82713&glb_olk=10731928&glb_od=100021523291665079bc6hpre1710446&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.gearbest.com%2Fled-strips-c_11267%2F%3Fpage%3D1%26testKey%3Dold%26attr%3D2234-10290&glb_cl=https%3A%2F%2Fwww.gearbest.com%2Fled-strips-c_11267%2F%3Fpage%3D1%26testKey%3Dold%26attr%3D2234-10290&glb_pl=https%3A%2F%2Fwww.gearbest.com%2Fled-strips-c_11267%2F HTTP/1.1\"^A^200^A^372^A^\"https://www.gearbest.com/led-strips-c_11267/?page=1&testKey=old&attr=2234-10290\"^A^\"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko\"^A^s.logsss.com^A^89.222.164.190, 10.21.240.50, 2.21.240.36, 10.20.245.117, 2.23.84.70^A^89.222.164.190^A^RU^A^Russian Federation^A^ru-RU^A^1537976477";
    String log = "172.31.60.246^A^-^A^[16/Oct/2018:07:00:45 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_tm=1539673419106&glb_oi=ae6527458afec8304759&glb_u=13266028&glb_d=10002&glb_b=e&glb_s=e013&glb_plf=m&glb_dc=1303&glb_x=MESSAGE&glb_ubcta={%22name%22:%22Ahorre%20hasta%20un%2060%%20de%20descuento%20|%20Los%20suministros%20de%20salud%20y%20belleza%22,%22type%22:%22gbsite_custom%22}&glb_w=11480&glb_od=orbngnmgebea1526370607589&glb_osr_referrer=https%3A%2F%2Fwww.google.com%2F&glb_osr_landing=https%3A%2F%2Fm-es.gearbest.com%2F&glb_cl=https%3A%2F%2Fuserm.gearbest.com%2Findex%23%2Fmessage%2Fsort%3Findex%3D2%26source%3Dgbsite_custom&glb_pl=https%3A%2F%2Fuserm.gearbest.com%2Findex HTTP/1.1\"^A^200^A^372^A^\"https://userm.gearbest.com/index\"^A^\"Mozilla/5.0 (Linux; Android 8.0.0; SM-G930F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36\"^A^s.logsss.com^A^37.29.227.88, 10.22.126.223, 2.22.126.213, 10.122.242.85, 92.122.242.124^A^37.29.227.88^A^ES^A^Spain^A^es-ES,es;q=0.9,en;q=0.8^A^1539673245";

    String s = "https://s.logsss.com/_ubc.gif?glb_t=ie&glb_w=10836&glb_tm=1538027308072&glb_pm=md&glb_ubcta=[{%22mdlc%22:%22B_3%22,%22mdID%22:%22812%22}]&glb_plf=pc&glb_oi=m5gi5dd08kg8iqrtpekmggbo46&glb_d=10013&glb_b=a&glb_k=sz01&glb_dc=1301&glb_od=100131527644242901364134&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.zaful.com%2F&glb_cl=https%3A%2F%2Fwww.zaful.com%2F";

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
        System.out.println(NginxLogConvertUtil.getNginxLogParameters(log));
        System.out.println(NginxLogConvertUtil.getNginxLogParameters(log));
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
