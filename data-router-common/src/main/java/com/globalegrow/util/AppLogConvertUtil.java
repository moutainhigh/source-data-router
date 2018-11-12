package com.globalegrow.util;

import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.globalegrow.util.NginxLogConvertUtil.getTimestamp;
import static com.globalegrow.util.NginxLogConvertUtil.getUrlParams;


public class AppLogConvertUtil {

    public static final String PARAMETERS_PATTERN = "_app.gif\\??(.*)HTTP";
    //public static final String TIMESTAMP_PATTERN = "\\^A\\^\\d{10}";

    public static final String TIMESTAMP_KEY = "timestamp";

    /**
     * 将原始埋点数据转换为 map
     *
     * @param log
     * @return
     */
    public static Map<String, Object> getAppLogParameters(String log) {
        if (log.contains("/_app.gif")) {
            Pattern p = Pattern.compile(PARAMETERS_PATTERN);
            Matcher m = null;
            try {
                m = p.matcher(URLDecoder.decode(log, "utf-8"));
            } catch (Exception e) {
                m = p.matcher(log);
            }
            String requestStr = "";

            while (m.find()) {
                requestStr = m.group();
            }
            Map<String, Object> result = getStringObjectMap(log, requestStr, TIMESTAMP_KEY);
            if (result != null) return result;

        }
        return null;
    }

    static Map<String, Object> getStringObjectMap(String log, String requestStr, String timestampKey) {
        if (StringUtils.isNotEmpty(requestStr)) {
            if (requestStr.endsWith(" HTTP")) {
                requestStr = requestStr.substring(0, requestStr.lastIndexOf(" HTTP"));
            }

            Map<String, Object> result = getUrlParams(requestStr.replaceAll("%20&%20", " %26 "));
            if (result.size() > 0) {
                result.put(timestampKey, getTimestamp(log));
                return result;
            }

            return result;
        }
        return null;
    }

}
