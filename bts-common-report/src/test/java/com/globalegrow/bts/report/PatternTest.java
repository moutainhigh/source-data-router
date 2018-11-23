package com.globalegrow.bts.report;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.globalegrow.util.NginxLogConvertUtil.TIMESTAMP_PATTERN;

public class PatternTest {

    public static final String URL_PARAMETERS_PATTREN = "(.*?)=(.*?)}&";
    public static final String URL_PARAMETERS_UBC_PATTREN = "glb_ubcta=(.*?)&";
    public static final String PARAMETERS_PATTERN = "_ubc.gif\\??(.*)HTTP";

    String log = "172.31.63.147^A^-^A^[12/Nov/2018:04:54:20 +0000]^A^\"GET /_ubc.gif?glb_t=ic&glb_w=6590&glb_tm=1541998460845&glb_pm=mp&glb_skuinfo={%22sku%22:%22285793802%22,%22price%22:%2233.99%22,%22discount_mark%22:%2242OFF%22,%22pam%22:%220%22,%22pc%22:%22140%22}&glb_ubcta={%22rank%22:2,%22sckw%22:%22Sweaters%20&%20Cardigans%22,%22sk%22:%22D%22,%22fmd%22:%22mp%22}&glb_filter={%22view%22:60,%22sort%22:%22Recommend%22,%22page%22:1}&glb_x=sku&glb_plf=pc&glb_oi=sn3jbhj7a9ctsumlk69bbdrmr3&glb_d=10013&glb_s=b01&glb_b=b&glb_p=140&glb_dc=1301&glb_olk=228038&glb_od=1001315419981523779bbdrmr338294&glb_osr_referrer=https%3A%2F%2Fwww.google.com%2F&glb_osr_landing=https%3A%2F%2Fwww.zaful.com%2Fpromotion%2Fthe-menswear-guide-745.html%3Flkid%3D228038%26gclid%3DEAIaIQobChMI_se33YbO3gIVg9lkCh29PArWEAAYASAAEgI5pfD_BwE&glb_cl=https%3A%2F%2Fwww.zaful.com%2Fmen-sweaters-cardigans-e_140%2F&glb_pl=https%3A%2F%2Fwww.zaful.com%2Fw%2Fwinter%2Fe_118%2F HTTP/1.1\"^A^200^A^372^A^\"https://www.zaful.com/men-sweaters-cardigans-e_140/\"^A^\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36\"^A^s.logsss.com^A^24.5.91.206, 10.233.61.154, 63.233.61.142, 10.237.221.142, 204.237.221.212^A^24.5.91.206^A^US^A^United States^A^en-US,en;q=0.9,it-IT;q=0.8,it;q=0.7,en-CA;q=0.6^A^1541998460";

    @Test
    public void test() throws UnsupportedEncodingException, MalformedURLException {
        long start = System.currentTimeMillis();
        Pattern p = Pattern.compile(PARAMETERS_PATTERN);
        Matcher m = p.matcher(log);
        String requestStr = "";

        while (m.find()) {
            requestStr = m.group();
        }
        if (requestStr.endsWith(" HTTP")) {
            requestStr = requestStr.substring(0, requestStr.lastIndexOf(" HTTP"));
        }
        System.out.println(requestStr);

        Pattern parameter = Pattern.compile(URL_PARAMETERS_PATTREN);
        Matcher mparameter = parameter.matcher(requestStr);
        List<String> parameters = new ArrayList<>();

        while (mparameter.find()) {
            System.out.println(mparameter.group());
            String group = mparameter.group();
            parameters.add(group.replaceAll("&", ""));
        }

        System.out.println(parameters);

        for (int i = 0; i < parameters.size(); i++) {
            String[] p1 = parameters.get(i).split("=");
            System.out.println(p1[0] + "    " +p1[1]);

        }

    }


}
