package com.globalegrow;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class StringTest {

    @Test
    public void test() {
       System.out.println( "d01".equals("d01"));
    }

    @Test
    public void loginLogTest() throws UnsupportedEncodingException {
        String log = "glb_t=ic\n" +
                "glb_w=2097641\n" +
                "glb_tm=1534299394874\n" +
                "glb_x=SIGNIN\n" +
                "glb_ubcta={%22p%22:%22z-747%22}\n" +
                "glb_plf=pc\n" +
                "glb_bts=%7B%22plancode%22%3A%22rgcart%22%2C%22versionid%22%3A%2246%22%2C%22bucketid%22%3A%223%22%2C%22planid%22%3A%2220%22%2C%22policy%22%3A%220%22%7D\n" +
                "glb_oi=lh4khl83ej9i4seut30k8p9mn2\n" +
                "glb_d=10007\n" +
                "glb_s=f01\n" +
                "glb_b=f\n" +
                "glb_dc=1301\n" +
                "glb_od=1000715342971986070k8p9mn2632199\n" +
                "glb_osr_referrer=originalurl\n" +
                "glb_osr_landing=https%3A%2F%2Fcart.rosegal.com%2Fm-flow-a-cart.htm\n" +
                "glb_cl=https%3A%2F%2Flogin.rosegal.com%2Fm-users-a-sign.htm%3Fflow%3Dcheckout\n" +
                "glb_pl=https%3A%2F%2Fcart.rosegal.com%2Fm-flow-a-cart.htm";

        Map<String, String> map = new HashMap<>();
        String[] logs = log.split("\n");
        for (String s : logs) {
            map.put(s.split("=")[0], s.split("=")[1]);
        }
        System.out.println(map);

        String glbS = String.valueOf(map.get("glb_s"));
        String glbPlf = String.valueOf(map.get("glb_plf"));
        String glbX = String.valueOf(map.get("glb_x"));
        String glbOd = String.valueOf(map.get("glb_od"));
        String glbCl = String.valueOf(map.get("glb_cl"));

        System.out.println(("f01".equals(glbS) && "pc".equals(glbPlf) && "SIGNIN".equals(glbX)
                && "https://login.rosegal.com/m-users-a-sign.htm?flow=checkout".equals(URLDecoder.decode(glbCl, "utf-8"))));

        String btsInfo = String.valueOf(map.get("glb_bts"));


    }

}
