package com.globalegrow.util;

import com.globalegrow.util.NginxLogConvertUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum UrlParametersHandle {
    commonParameters(NginxLogConvertUtil.parameter){
        @Override
        public Map<String, Object> result(String log) {
            Map<String, Object> map = new HashMap(0);
            List<String> parameters = new ArrayList<>();

            Matcher mparameter = this.getPattern().matcher(log);
            while (mparameter.find()) {
                String group = mparameter.group();
                if (!group.contains("{") && !group.contains("}")) {
                    parameters.add(group.substring(0, group.lastIndexOf("&")));
                }
            }
            handleUrlParameters(parameters, map);
            return map;
        }
    },jsonParameters(NginxLogConvertUtil.parameterJson){
        @Override
        public Map<String, Object> result(String log) {
            Matcher mparameter = this.getPattern().matcher(log);
            List<String> parameters = new ArrayList<>();

            while (mparameter.find()) {
                String group = mparameter.group();

                parameters.add(group.substring(0, group.lastIndexOf("&")));

            }


            Map<String, Object> map = new HashMap(0);
            if (StringUtils.isBlank(log)) {
                return map;
            }
            handleUrlParameters(parameters, map);
            return map;
        }
    },timestampParameters(NginxLogConvertUtil.timestamp){
        @Override
        public Map<String, Object> result(String log) {
            long start = System.currentTimeMillis();
            Map<String, Object> objectMap = new HashMap<>();
            Matcher m = null;
            try {
                m = this.getPattern().matcher(URLDecoder.decode(log, "utf-8"));
            } catch (Exception e) {
                m = this.getPattern().matcher(log);
            }
            String requestStr = "";

            while (m.find()) {
                requestStr = m.group();
            }
            if (StringUtils.isNotEmpty(requestStr)) {
                String pattern = "\\d";
                Pattern p1 = Pattern.compile(pattern);
                Matcher m1 = p1.matcher(requestStr);
                StringBuilder time = new StringBuilder();
                while (m1.find()) {
                    time.append(m1.group());
                }
                objectMap.put(NginxLogConvertUtil.TIMESTAMP_KEY, Long.valueOf(time.toString()) * 1000);
            }
            System.out.println(System.currentTimeMillis() - start);
            return objectMap;
        }
    }
    ;

    public static void handleUrlParameters(List<String> parameters, Map<String, Object> map) {
        for (int i = 0; i < parameters.size(); i++) {
            String[] p = parameters.get(i).split("=");
            if (p.length == 2) {
                try {
                    String key = p[0];
                    String value = URLDecoder.decode(p[1].replaceAll("\t", "").replaceAll("\\\\x", "%"), "utf-8");
                    if (key.startsWith("_ubc.gif?")) {
                        map.put(key.replace("_ubc.gif?", ""), value);
                    } else if (key.startsWith("_app.gif?")) {
                        map.put(key.replace("_app.gif?", "").toLowerCase(), value);
                    } else {
                        map.put(p[0].toLowerCase(), NginxLogConvertUtil.valueHex(value));
                    }
                } catch (UnsupportedEncodingException e) {
                    map.put(p[0].toLowerCase(), p[1]);
                }
            }
        }

    }

    UrlParametersHandle(Pattern pattern) {
        this.pattern = pattern;
    }

    private Pattern pattern;

    public Pattern getPattern() {
        return pattern;
    }

    public abstract Map<String,Object> result(String log);
}
