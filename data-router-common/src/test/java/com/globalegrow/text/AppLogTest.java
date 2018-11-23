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
    String log = "172.31.63.147^A^-^A^[21/Nov/2018:08:49:04 +0000]^A^\"GET /_app.gif?re_targeting_conversion_type=&is_retargeting=false&app_id=id1078789949&platform=ios&event_type=in-app-event&attribution_type=regular&event_time=2018-11-21%2008%3A49%3A01&event_time_selected_timezone=2018-11-21%2016%3A49%3A01.668%2B0800&event_name=af_add_to_wishlist&event_value=%7B%22af_content_id%22%3A%22274478201%22%2C%22af_content_type%22%3A%22product%22%2C%22af_inner_mediasource%22%3A%22category_%D8%AB%D9%88%D8%A8%20%D8%B0%D9%88%20%D9%82%D8%B7%D8%B9%D8%AA%D9%8A%D9%86%22%2C%22af_version_id%22%3A%22%22%2C%22af_bucket_id%22%3A%22%22%2C%22af_plan_id%22%3A%22%22%2C%22af_content_category%22%3A%22%5C%2F%D9%86%D8%B3%D8%A7%D8%A1%5C%2F%D8%A8%D9%84%D8%A7%D9%8A%D8%B2%5C%2F%D8%AB%D9%88%D8%A8%20%D8%B0%D9%88%20%D9%82%D8%B7%D8%B9%D8%AA%D9%8A%D9%86%22%2C%22af_price%22%3A%2225.78%22%2C%22af_changed_size_or_color%22%3A%220%22%2C%22af_currency%22%3A%22USD%22%7D&currency=USD&selected_currency=USD&revenue_in_selected_currency=&cost_per_install=&click_time=2018-11-17%2020%3A58%3A10&click_time_selected_timezone=2018-11-18%2004%3A58%3A10.801%2B0800&install_time=2018-11-21%2007%3A22%3A17&install_time_selected_timezone=2018-11-21%2015%3A22%3A17.265%2B0800&agency=&campaign=%5BApp%5D%201024%20%E4%B8%AD%E4%B8%9C%20sa%2Bkw%2Bae%20ios%20%E6%9C%8D%E8%A3%85&media_source=snapchat_int&af_sub1=&af_sub2=&af_sub3=&af_sub4=&af_sub5=&af_siteid=&click_url=&fb_campaign_id=&fb_campaign_name=&fb_adset_id=&fb_adset_name=&fb_adgroup_id=&fb_adgroup_name=&country_code=SA&city=Al-Jubayl&ip=95.218.60.82&wifi=true&language=en-SA&appsflyer_device_id=1542499098672-4585445&customer_user_id=18064787&idfa=BBEA9540-21D8-4D46-A92D-51F252939ED9&mac=&device_name=&device_type=iPhone%20X&os_version=12.1&sdk_version=v4.8.9&app_version=4.3.0&http_referrer=&idfv=7EA96402-0C19-40CB-9B42-77CBD3E3C4F2&app_name=ZAFUL%20-%20My%20Fashion%20Story&download_time=2018-11-17%2020%3A58%3A18&download_time_selected_timezone=2018-11-18%2004%3A58%3A18.000%2B0800&af_cost_currency=&af_cost_value=&af_cost_model=&af_c_id=3b41a957-78a5-4d7b-a3b5-4f513f6794b8&af_adset=1107%20sa%20%E5%A5%B3%E8%A3%85%202&af_adset_id=17376d84-fc94-49a7-ba86-ed905047b658&af_ad=1107%20%E9%98%BF%E8%AF%AD%20coat%20%E9%AD%8F%E6%98%95%E8%B5%9F%20%E6%94%B9&af_ad_id=85739d0f-f958-44e8-b121-5e63f972a87d&af_ad_type=&af_channel=&af_keywords=&bundle_id=com.zaful.Zaful&attributed_touch_type=click&attributed_touch_time=2018-11-17%2020%3A58%3A10 HTTP/1.1\"^A^200^A^372^A^\"-\"^A^\"http-kit/2.0\"^A^s.logsss.com^A^34.248.243.107, 10.221.222.58, 88.221.222.130, 10.55.59.245, 23.55.59.236^A^34.248.243.107^A^US^A^United States^A^-^A^1542790144";


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
