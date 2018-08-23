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
    String log = "172.31.49.32^A^-^A^[21/Aug/2018:00:00:00 +0000]^A^\"GET /_app.gif?re_targeting_conversion_type=&is_retargeting=false&app_id=com.globalegrow.app.gearbest&platform=android&event_type=in-app-event&attribution_type=organic&event_time=2018-08-21%2000%3A00%3A00&event_time_selected_timezone=2018-08-21%2008%3A00%3A00.060%2B0800&event_name=af_view_product&event_value=%7B%22af_content_type%22%3A%22%20Computadores%20e%20Redes%5C%2F%20Perif%C3%A9ricos%20de%20computador%5C%2F%20Cart%C3%B5es%20de%20mem%C3%B3ria%22%2C%22af_content_ids%22%3A%22187807901%22%7D&currency=USD&selected_currency=USD&revenue_in_selected_currency=&cost_per_install=&click_time=&click_time_selected_timezone=&install_time=2018-05-05%2011%3A25%3A47&install_time_selected_timezone=2018-05-05%2019%3A25%3A47.329%2B0800&agency=&media_source=Organic&campaign=&fb_campaign_id=&fb_campaign_name=&fb_adset_name=&fb_adset_id=&fb_adgroup_id=&fb_adgroup_name=&af_siteid=&country_code=BR&city=Aracaju&ip=143.202.45.192&wifi=true&language=portugu%C3%AAs&appsflyer_device_id=1525484637139-1672643704481727944&customer_user_id=18453477&android_id=3fd1aedb059466fb&imei=&advertising_id=979ff92f-465c-4110-8b4d-762a22b6234b&mac=&device_brand=lge&device_model=LG-M250&os_version=7.0&sdk_version=v4.8.11&app_version=3.6.0&operator=Vivo&carrier=VIVO&af_sub1=&af_sub2=&af_sub3=&af_sub4=&af_sub5=&click_url=&http_referrer=&app_name=GearBest%20Online%20Shopping&download_time=2018-05-05%2001%3A43%3A57&download_time_selected_timezone=2018-05-05%2009%3A43%3A57.000%2B0800&af_keywords=&bundle_id=com.globalegrow.app.gearbest&attributed_touch_type=&attributed_touch_time= HTTP/1.1\"^A^200^A^372^A^\"-\"^A^\"http-kit/2.0\"^A^s.logsss.com^A^34.240.29.179, 10.221.222.58, 88.221.222.74, 10.55.59.245, 23.55.59.236^A^34.240.29.179^A^US^A^United States^A^-^A^1534809600";


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
