import com.globalegrow.dy.controller.AppEventEnums;
import com.globalegrow.dy.controller.SiteUtil;
import com.globalegrow.dy.utils.JacksonUtil;
import com.globalegrow.dy.utils.NginxLogConvertUtil;
import io.searchbox.core.Index;
import io.searchbox.params.Parameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class HistoryDataInstallTest {


    @Test
    public void test() {
        File file = new File("E:\\浏览器下载\\1023.csv");
        int i = 0;
        try (LineIterator it = FileUtils.lineIterator(file, "UTF-8")) {
            while (it.hasNext()) {
                String line = it.nextLine();

                if (line.indexOf("\"{\"") > 0 && line.indexOf("\"}\"") > 0) {
                    String jsonValue = line.substring(line.indexOf("\"{\""), line.indexOf("\"}\"") + 3);
                    //jsonValue = jsonValue.replace("\"{\"", "{").replace("\"}\"", "}").replaceAll("\\\\\"","");
                    String s = StringEscapeUtils.escapeJson(jsonValue);
                    String s1 = s.replaceAll("\\\\\"\\\\\"", "\\\"");
                    String s2 = s1.replaceAll("\\\\\"", "");
                    //System.out.println(s2);
                    Map<String, Object> eventValueMap = null;
                    try {
                        eventValueMap = JacksonUtil.readValue(s2, Map.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (eventValueMap != null) {
                        String eventName = line.substring(0, line.indexOf("\"{\"") - 1);
                        if (eventName.equals("af_view_product")) {
                            i++;
                        }
                        String valueNeeded = AppEventEnums.valueOf(eventName).getEventValueFromEventValue(eventValueMap);
                        String jsonAfter = line.substring(line.indexOf("\"}\"") + 4);
                        //System.out.println(value.substring(0,value.indexOf("\"{\"")-1));
                        //System.out.println(value.substring(value.indexOf("\"}\"") +4));
                        String[] deviceIdAndDateAndSite = jsonAfter.split(",");
                        String deviceId = deviceIdAndDateAndSite[0];
                        String userId = deviceIdAndDateAndSite[1];
                        //System.out.println(deviceId);
                        String date = deviceIdAndDateAndSite[2];
                        String site = SiteUtil.getAppSite(deviceIdAndDateAndSite[3]);
                        String platform = deviceIdAndDateAndSite[4];
                        if ("1540268234170-2053145178484571429".equals(deviceId)) {
                            System.out.println(line);
                            System.out.println(eventValueMap);
                        }
                       /* if (StringUtils.isNotEmpty(valueNeeded) && "zaful".equals(site)) {

                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                            Long timestamp = null;
                            if (date.contains("/")) {
                                try {
                                    timestamp = dateFormat.parse(date).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (date.contains("-")) {
                                try {
                                    timestamp = dateFormat2.parse(date).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (StringUtils.isNotEmpty(valueNeeded) && eventValueMap != null && eventValueMap.size() > 0 && timestamp != null) {
                                for (String s123 : valueNeeded.split(",")) {
                                    Map<String, Object> eventDataRow = new HashMap<>();
                                    eventDataRow.put("event_name", eventName);
                                    eventDataRow.put("event_value", s123);
                                    eventDataRow.put("user_id", userId);
                                    eventDataRow.put("device_id", deviceId);
                                    eventDataRow.put("platform", platform);
                                    eventDataRow.put("site", site);
                                    eventDataRow.put(NginxLogConvertUtil.TIMESTAMP_KEY, timestamp);

                                    System.out.println(eventDataRow);

                                }
                            }

                        }*/
                    }

                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(i);

    }

}
