/*
import com.globalegrow.dy.dto.UserActionDto;
import com.globalegrow.dy.dto.UserActionEsDto;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.utils.AppLogConvertUtil;
import com.globalegrow.dy.utils.JacksonUtil;
import com.globalegrow.dy.utils.NginxLogConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonHandleTest {

    public String logString = "172.31.20.159^A^-^A^[18/Oct/2018:08:38:05 +0000]^A^\"GET /_app.gif?re_targeting_conversion_type=&is_retargeting=false&app_id=id1078789949&platform=android&event_type=in-app-event&attribution_type=regular&event_time=2018-10-22%2008%3A38%3A05&event_time_selected_timezone=2018-10-18%2016%3A38%3A05.114%2B0800&event_name=af_add_to_bag&event_value=%7B%22af_content_id%22%3A%22281590701%2C250000001%2C250000002%2C250000003%2C250000004%2C250000005%2C250000006%2C250000007%2C250000008%2C250000009%2C250000010%2C250000011%2C250000012%2C250000013%2C250000014%2C250000015%2C250000016%2C250000017%2C250000018%2C250000019%2C250000020%2C250000021%2C250000022%2C250000023%2C250000024%2C250000025%22%2C%22af_content_type%22%3A%22product%22%2C%22af_changed_size_or_color%22%3A%220%22%2C%22af_inner_mediasource%22%3A%22category_Two-Piece%20Outfits%22%2C%22af_version_id%22%3A%22%22%2C%22af_bucket_id%22%3A%22%22%2C%22af_plan_id%22%3A%22%22%2C%22af_content_category%22%3A%22%5C%2FWomen%5C%2FTops%5C%2FTwo-Piece%20Outfits%22%2C%22af_price%22%3A%2218.49%22%2C%22af_quantity%22%3A%221%22%2C%22af_currency%22%3A%22USD%22%7D&currency=USD&selected_currency=USD&revenue_in_selected_currency=&cost_per_install=&click_time=2018-07-08%2023%3A38%3A25&click_time_selected_timezone=2018-07-09%2007%3A38%3A25.170%2B0800&install_time=2018-07-08%2023%3A39%3A03&install_time_selected_timezone=2018-07-09%2007%3A39%3A03.769%2B0800&agency=&campaign=%5BAPP%5D%200530%20DE%20%E5%A5%B3%E8%A3%85&media_source=snapchat_int&af_sub1=&af_sub2=&af_sub3=&af_sub4=&af_sub5=&af_siteid=&click_url=&fb_campaign_id=&fb_campaign_name=&fb_adset_id=&fb_adset_name=&fb_adgroup_id=&fb_adgroup_name=&country_code=DE&city=Frankfurt%20Am%20Main&ip=87.139.146.154&wifi=true&language=de-DE&appsflyer_device_id=wangzhongfu_test&customer_user_id=14359714&idfa=1A070A8A-E6A0-4BB0-B2AD-C2A1E26094CF&mac=&device_name=&device_type=iPhone%208%20Plus&os_version=12.0.1&sdk_version=v4.8.4&app_version=3.9.0&http_referrer=&idfv=F40E9DD2-B04A-4AE8-BC7E-9641EE0BA9DB&app_name=ZAFUL%20-%20My%20Fashion%20Story&download_time=2018-07-08%2023%3A38%3A38&download_time_selected_timezone=2018-07-09%2007%3A38%3A38.000%2B0800&af_cost_currency=&af_cost_value=&af_cost_model=&af_c_id=317f8c0a-3ff1-4010-999d-7e90b3d1e494&af_adset=0530%20de%20%E5%A5%B3%E8%A3%85%201&af_adset_id=32dab873-7233-4087-be93-98d2c8c79b6a&af_ad=0505%20%E8%8B%B1%E8%AF%AD%20bikini%20%E6%96%B9%E5%BB%BA%E6%A2%85&af_ad_id=693d72bc-7ea8-422a-8046-d00d00e4ada4&af_ad_type=&af_channel=&af_keywords=&bundle_id=com.zaful.Zaful&attributed_touch_type=click&attributed_touch_time=2018-07-08%2023%3A38%3A25HTTP/1.1\"^A^200^A^372^A^\"-\"^A^\"http-kit/2.0\"^A^s.logsss.com^A^52.213.26.244,10.221.222.74, 88.221.222.130, 10.55.59.236, 23.55.59.245^A^52.213.26.244^A^IE^A^Ireland^A^-^A^1540450604";

    @Test
    public void testRedisDataHandle() {
        List<String> strings = new ArrayList<>();
        List<UserActionDto> list = new ArrayList<>();
        Map value = AppLogConvertUtil.getAppLogParameters(logString);
        if (value != null) {
            String eventName = String.valueOf(value.get("event_name"));
            String eventValue = String.valueOf(value.get("event_value"));
            Map<String, Object> eventValueMap = null;
            try {
                if (eventValue.startsWith("{:")) {
                    eventValue = NginxLogConvertUtil.handleBadJson(eventValue);
                }
                eventValueMap = JacksonUtil.readValue(eventValue, Map.class);
            } catch (Exception e) {
                //logger.error("解析 json 数据出错: {}", eventValue, e);
            }
            if (StringUtils.isNotEmpty(eventName) && eventValueMap != null) {
                Long timestamp = (Long) value.get(NginxLogConvertUtil.TIMESTAMP_KEY);
                String date = DateFormatUtils.format(timestamp, "yyyy-MM-dd");
                    String deviceId = String.valueOf(value.get("appsflyer_device_id"));
                    String userId = String.valueOf(value.get("customer_user_id"));
                    String platform = String.valueOf(value.get("platform"));
                    String appName = String.valueOf(value.get("app_name"));

                    try {
                        String valueNeeded = AppValueEventEnums.valueOf(eventName).getEventValueFromEventValue(eventValueMap);
                        if (StringUtils.isNotEmpty(valueNeeded) && eventValueMap != null && eventValueMap.size() > 0) {
                            for (String s : valueNeeded.split(",")) {
                                Map<String, Object> eventDataRow = new HashMap<>();
                                eventDataRow.put("event_name", eventName);
                                eventDataRow.put("event_value", s);
                                eventDataRow.put("user_id", userId);
                                eventDataRow.put("device_id", deviceId);
                                eventDataRow.put("platform", platform);
                                eventDataRow.put("site", "zaful");
                                eventDataRow.put(NginxLogConvertUtil.TIMESTAMP_KEY, timestamp);
                                String key = "dy_rt_p_" + deviceId + "_" + date;
                                strings.add(JacksonUtil.toJSon(eventDataRow));
                            }

                        }
                    } catch (IllegalArgumentException e) {
                        //logger.error("event {} not support yet!", eventName, e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


            }
        }
        long mapStart = System.currentTimeMillis();
        strings.parallelStream().map(s -> {
            try {
                if (s.startsWith("\"")) {
                    s= s.replaceFirst("\"", "");
                }
                if (s.endsWith("\"")) {
                    s=s.substring(0, s.lastIndexOf("\""));
                }
                return JacksonUtil.readValue(s.replaceAll("\\\\", ""), UserActionEsDto.class);
            } catch (Exception e) {
                return null;
            }
        }).filter(d -> d!=null).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(UserActionEsDto :: getDevice_id))
                .entrySet().stream().forEach(a -> {
                    long mapEnd = System.currentTimeMillis();
                    System.out.println(mapEnd - mapStart);
            handleUserActionData(list, a);
        });


    }

    static void handleUserActionData(List<UserActionDto> list, Map.Entry<String, List<UserActionEsDto>> a) {
        UserActionDto userActionDto = new UserActionDto();
        userActionDto.setCookieId(a.getKey());
        List<UserActionEsDto> data = a.getValue();
        if (data != null && data.size() > 0) {
            userActionDto.setUserId(data.get(0).getUser_id());
            data.stream().collect(Collectors.groupingBy(UserActionEsDto :: getEvent_name))
                    .entrySet().stream().forEach(e -> {
                try {
                    AppEventEnums.valueOf(e.getKey()).handleEventResult(userActionDto, e.getValue());
                } catch (IllegalArgumentException e1) {

                }
            });
        }
        list.add(userActionDto);
    }



}
*/
