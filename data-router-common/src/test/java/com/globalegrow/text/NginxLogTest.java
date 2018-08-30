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
    String log = "172.31.49.32^A^-^A^[27/Aug/2018:02:02:48 +0000]^A^\"GET /_ubc.gif?glb_t=ie&glb_w=12573&glb_tm=1535335367251&glb_pm=mp&glb_filter={\\x22view\\x22:60,\\x22sort\\x22:\\x22Recommend\\x22,\\x22page\\x22:1}&glb_bts=%7B%22plancode%22%3A%22recommend%22%2C%22versionid%22%3A%22105%22%2C%22bucketid%22%3A%227%22%2C%22planid%22%3A%2245%22%2C%22policy%22%3A%22B%22%7D&glb_ubcta=[{\\x22sku\\x22:\\x22176061802\\x22},{\\x22sku\\x22:\\x22217726712\\x22},{\\x22sku\\x22:\\x22227532907\\x22},{\\x22sku\\x22:\\x22238426902\\x22},{\\x22sku\\x22:\\x22185278301\\x22},{\\x22sku\\x22:\\x22266027502\\x22},{\\x22sku\\x22:\\x22232930507\\x22},{\\x22sku\\x22:\\x22263587701\\x22}]&glb_plf=pc&glb_oi=22cg4n81n81d3e5dfmmpl8smo0&glb_d=10013&glb_s=b01&glb_b=b&glb_p=14-1&glb_k=sz01&glb_dc=1301&glb_olk=14665422&glb_od=100131503183298048a82k1bh0021636224177064417&glb_osr=ol%3Doriginalurl%7Chref%3Dhttps%3A%2F%2Fwww.zaful.com%2Fb%2Fswimwear-e_14%2F%3Flkid%3D14665422%26admitad_uid%3Da7511d976e80dbe356a34a64aa3d6719%26utm_source%3Dadmitad&glb_cl=https%3A%2F%2Fwww.zaful.com%2Fb%2Fswimwear-e_14%2F%3Flkid%3D14665422%26admitad_uid%3Da7511d976e80dbe356a34a64aa3d6719%26utm_source%3Dadmitad&glb_pl=https%3A%2F%2Fwww.zaful.com%2Fswimwear-e_14%2F%3Flkid%3D14665422%26admitad_uid%3Da7511d976e80dbe356a34a64aa3d6719%26utm_source%3Dadmitad HTTP/1.1\"^A^200^A^372^A^\"https://www.zaful.com/b/swimwear-e_14/?lkid=14665422&admitad_uid=a7511d976e80dbe356a34a64aa3d6719&utm_source=admitad\"^A^\"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko\"^A^s.logsss.com^A^184.89.82.120, 23.215.15.83, 10.50.48.159, 23.50.48.182^A^184.89.82.120^A^US^A^United States^A^en-US^A^1535335368";

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
