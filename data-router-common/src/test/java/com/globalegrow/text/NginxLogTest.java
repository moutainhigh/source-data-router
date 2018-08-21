package com.globalegrow.text;

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
    String log = "172.31.49.32^A^-^A^[15/Aug/2018:04:45:52 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_w=4561759&glb_tm=1534308352320&glb_x=SIGNIN&glb_ubcta={%22p%22:%22x-273775603%22}&glb_plf=pc&glb_bts=%7B%22plancode%22%3A%22rgcart%22%2C%22versionid%22%3A%2246%22%2C%22bucketid%22%3A%223%22%2C%22planid%22%3A%2220%22%2C%22policy%22%3A%220%22%7D&glb_oi=lh4khl83ej9i4seut30k8p9mn2&glb_d=10007&glb_s=f01&glb_b=f&glb_dc=1301&glb_od=1000715342971986070k8p9mn2632199&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fcart.rosegal.com%2Fm-flow-a-cart.htm&glb_cl=https%3A%2F%2Flogin.rosegal.com%2Fm-users-a-sign.htm%3Fflow%3Dcheckout&glb_pl=https%3A%2F%2Fcart.rosegal.com%2Fm-flow-a-cart.htm HTTP/1.1\"^A^200^A^372^A^\"https://login.rosegal.com/m-users-a-sign.htm?flow=checkout\"^A^\"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0\"^A^s.logsss.com^A^114.113.243.226, 10.55.46.70, 23.55.46.63, 10.32.20.55, 104.84.150.37^A^114.113.243.226^A^HK^A^Hong Kong^A^zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2^A^1534308352";

    @Test
    public void utilTest() {
        System.out.println(NginxLogConvertUtil.getNginxLogParameters(log));
        System.out.println(NginxLogConvertUtil.getNginxLogParameters(log));
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
