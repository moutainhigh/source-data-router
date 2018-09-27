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
    String log = " 172.31.23.90^A^-^A^[26/Sep/2018:20:13:57 +0000]^A^\"GET /_app.gif?re_targeting_conversion_type=&is_retargeting=false&app_id=com.globalegrow.app.gearbest&platform=android&event_type=in-app-event&attribution_type=regular&event_time=2018-09-26%2020%3A13%3A55&event_time_selected_timezone=2018-09-27%2004%3A13%3A55.000%2B0800&event_name=af_purchase&event_value=%7B%3Aaf_content_id%20%22261010402%2C264262201%22%2C%20%3Aaf_price%20%221.97%2C6.08%22%2C%20%3Aaf_quantity%20%221%2C1%22%2C%20%3Aorders_ids%20%2218092600947315110451%22%2C%20%3Aaf_revenue%2011.21%2C%20%3Aaf_content_type%20%22BOLETO%22%2C%20%3Aaf_currency%20%22USD%22%7D&currency=USD&selected_currency=USD&revenue_in_selected_currency=&cost_per_install=&click_time=2018-09-26%2009%3A14%3A10&click_time_selected_timezone=2018-09-26%2017%3A14%3A10.064%2B0800&install_time=2018-09-26%2019%3A32%3A07&install_time_selected_timezone=2018-09-27%2003%3A32%3A07.233%2B0800&agency=&campaign=%5BUAC%5DBR%20Electronics-Android180824&media_source=googleadwords_int&af_sub1=&af_sub2=&af_sub3=&af_sub4=&af_sub5=&af_siteid=&click_url=&fb_campaign_id=&fb_campaign_name=&fb_adset_id=&fb_adset_name=&fb_adgroup_id=&fb_adgroup_name=&country_code=BR&city=Maua&ip=177.133.163.214&wifi=&language=&appsflyer_device_id=1537990322285-5695151094453348220&customer_user_id=&android_id=&imei=&advertising_id=124ac36d-fb3b-45d4-b75d-820f7d872af3&mac=&device_brand=&device_model=&os_version=&sdk_version=&app_version=&operator=&carrier=&http_referrer=&app_name=&download_time=2018-09-26%2019%3A32%3A02&download_time_selected_timezone=2018-09-27%2003%3A32%3A02.285%2B0800&af_cost_currency=&af_cost_value=&af_cost_model=&af_c_id=1526642551&af_adset=&af_adset_id=&af_ad=&af_ad_id=&af_ad_type=ClickToDownload&af_channel=UAC_Display&af_keywords=&bundle_id=com.globalegrow.app.gearbest&attributed_touch_type=impression&attributed_touch_time=2018-09-26%2009%3A14%3A10 HTTP/1.1\"^A^200^A^372^A^\"-\"^A^\"http-kit/2.0\"^A^s.logsss.com^A^52.209.235.239, 10.221.222.58, 88.221.222.146, 10.55.59.236, 23.55.59.245^A^52.209.235.239^A^IE^A^Ireland^A^-^A^1537992837";

    String s = "https://s.logsss.com/_ubc.gif?glb_t=ie&glb_w=10836&glb_tm=1538027308072&glb_pm=md&glb_ubcta=[{%22mdlc%22:%22B_3%22,%22mdID%22:%22812%22}]&glb_plf=pc&glb_oi=m5gi5dd08kg8iqrtpekmggbo46&glb_d=10013&glb_b=a&glb_k=sz01&glb_dc=1301&glb_od=100131527644242901364134&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.zaful.com%2F&glb_cl=https%3A%2F%2Fwww.zaful.com%2F";

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
