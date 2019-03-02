package com.globalegrow.text;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppLogTest {

    String pattern = "(.*?)=(.*?)&";
    String pattrenParameters = "_app.gif\\??(.*)HTTP";
    String patternTime = "\\^A\\^\\d{10}";
    String log = "172.31.26.148^A^-^A^[16/Feb/2019:09:41:12 +0000]^A^\"GET /_app.gif?re_targeting_conversion_type=&is_retargeting=false&app_id=id1131090631&platform=ios&event_type=in-app-event&attribution_type=organic&event_time=2019-02-16%2009%3A41%3A10&event_time_selected_timezone=2019-02-16%2017%3A41%3A10.353%2B0800&event_name=af_view_product&event_value=%7B%22af_content_ids%22%3A%22209951604%22%2C%22af_content_type%22%3A%22Bags%20%26%20Shoes%5C%2FWomen%27s%20Bags%5C%2FHandbags%22%7D&currency=USD&selected_currency=USD&revenue_in_selected_currency=&cost_per_install=&click_time=&click_time_selected_timezone=&install_time=2018-11-01%2006%3A34%3A39&install_time_selected_timezone=2018-11-01%2014%3A34%3A39.000%2B0800&agency=&media_source=Organic&campaign=&af_siteid=&fb_campaign_id=&fb_campaign_name=&fb_adset_name=&fb_adset_id=&fb_adgroup_id=&fb_adgroup_name=&country_code=CN&city=Shenzhen&ip=183.39.157.7&wifi=false&language=zh-Hans-CN&appsflyer_device_id=1541082879264-4737105&customer_user_id=&idfa=&mac=&device_name=&device_type=iPhone%206%20Plus&os_version=12.1&sdk_version=v4.8.4&app_version=3.3.1&af_sub1=&af_sub2=&af_sub3=&af_sub4=&af_sub5=&click_url=&http_referrer=&idfv=4BAC7E7F-44E2-4637-B73B-B932C9085515&app_name=GearBest%20Online%20Shopping&download_time=2018-11-01%2006%3A34%3A39&download_time_selected_timezone=2018-11-01%2014%3A34%3A39.000%2B0800&af_keywords=&bundle_id=com.globalegrow.gearbest&attributed_touch_type=&attributed_touch_time= HTTP/1.1\"^A^200^A^372^A^\"-\"^A^\"http-kit/2.0\"^A^s.logsss.com^A^52.213.26.244, 10.212.108.22, 23.212.108.12, 10.101.136.151, 95.101.136.119^A^52.213.26.244^A^IE^A^Ireland^A^-^A^1550310072";


    @Test
    public void testAppLogUtil() {
        long start = System.currentTimeMillis();
        System.out.println(AppLogConvertUtil.getAppLogParameters(log));
        System.out.println(System.currentTimeMillis() - start);
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

    }

    public static Map<String, Object> getUrlParams(String param) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");

            if (p.length == 2) {
                System.out.println(p[0] + " " + p[1]);
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
