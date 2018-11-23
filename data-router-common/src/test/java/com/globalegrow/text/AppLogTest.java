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
    String log = "172.31.20.159^A^-^A^[18/Oct/2018:08:38:05 +0000]^A^\"GET /_app.gif?re_targeting_conversion_type=&is_retargeting=false&app_id=id1131090631&platform=ios&event_type=in-app-event&attribution_type=organic&event_time=2018-10-23%2008%3A38%3A01&event_time_selected_timezone=2018-10-18%2016%3A38%3A01.974%2B0800&event_name=af_add_to_bag&event_value=%7B%22af_price%22%3A%2217.49%22%2C%22af_content_id%22%3A%22216664702%22%2C%22af_quantity%22%3A%221%22%2C%22af_changed_size_or_color%22%3A%220%22%2C%22af_content_category%22%3A%22%5C%2FWomen%5C%2FDresses%5C%2FMini%20Dresses%22%2C%22af_content_type%22%3A%22product%22%2C%22af_currency%22%3A%22USD%22%2C%22af_inner_mediasource%22%3A%22category_Mini%20Dresses%22%7D&currency=USD&selected_currency=USD&revenue_in_selected_currency=&cost_per_install=&click_time=&click_time_selected_timezone=&install_time=2018-08-20%2022%3A00%3A25&install_time_selected_timezone=2018-08-21%2006%3A00%3A25.000%2B0800&agency=&media_source=Organic&campaign=&af_siteid=&fb_campaign_id=&fb_campaign_name=&fb_adset_name=&fb_adset_id=&fb_adgroup_id=&fb_adgroup_name=&country_code=RU&city=Moskvich&ip=31.173.80.168&wifi=false&language=ru-RU&appsflyer_device_id=NQIQ44ecldcN4dd2dJ31iASSFFAA112233&customer_user_id=22579152&idfa=DF6B3AAA-CD20-45B9-9AAF-05A6C14913BE&mac=&device_name=&device_type=iPhone%205s&os_version=11.0.3&sdk_version=v4.8.4&app_version=3.2.0&af_sub1=&af_sub2=&af_sub3=&af_sub4=&af_sub5=&click_url=&http_referrer=&idfv=2C6E9CC9-F9F3-4C78-9CF2-548B49A375D5&app_name=ZAFUL%20-%20My%20Fashion%20Story&download_time=2018-08-20%2022%3A00%3A25&download_time_selected_timezone=2018-08-21%2006%3A00%3A25.000%2B0800&af_keywords=&bundle_id=com.globalegrow.gearbest&attributed_touch_type=&attributed_touch_time= HTTP/1.1\"^A^200^A^372^A^\"-\"^A^\"http-kit/2.0\"^A^s.logsss.com^A^52.48.69.35, 10.221.222.130, 88.221.222.146, 10.55.59.236, 23.55.59.245^A^52.48.69.35^A^IE^A^Ireland^A^-^A^1540256400";


    @Test
    public void testAppLogUtil() {
        System.out.println(AppLogConvertUtil.getAppLogParameters(log));
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
