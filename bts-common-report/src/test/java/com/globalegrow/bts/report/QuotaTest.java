package com.globalegrow.bts.report;

import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuotaTest {

    String log = "/_ubc.gif?glb_t=ie&glb_w=2313&glb_tm=1542353710246&glb_plf=pc&glb_filter={%22view%22:120,%22sort%22:%22recommend%22,%22page%22:1}&glb_bts={%22plancode%22:%22keyword%22,%22versionid%22:%22506%22,%22bucketid%22:%2269%22,%22planid%22:%22179%22,%22policy%22:%22B%22}&glb_oi=7ct0f7o7jv9murooerh2omfqq3&glb_d=10013&glb_s=b02&glb_b=b&glb_dc=1301&glb_od=100131527644242901364134&glb_osr_referrer=originalurl&glb_osr_landing=https%3A%2F%2Fwww.zaful.com%2F&glb_cl=https%3A%2F%2Fwww.zaful.com%2Fs%2Fhooded-sweatshirt%2F&glb_pl=https%3A%2F%2Fwww.zaful.com%2Fs%2Ffluffy-hoodie%2F%3Fpolicy_key%3DB HTTP/1.1\"^A^200^A^372^A^\"https://www.zaful.com/men-sweaters-cardigans-e_140/\"^A^\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36\"^A^s.logsss.com^A^24.5.91.206, 10.233.61.154, 63.233.61.142, 10.237.221.142, 204.237.221.212^A^24.5.91.206^A^US^A^United States^A^en-US,en;q=0.9,it-IT;q=0.8,it;q=0.7,en-CA;q=0.6^A^1541998460";

    @Test
    public void test() throws Exception {
        Map<String, Object> finalMap = this.finalJsonMap(log);

        System.out.println(finalMap);

        String s = JacksonUtil.toJSon(finalMap);

        ReadContext ctx  = JsonPath.parse(s);

        System.out.println(StringUtils.isNotEmpty(String.valueOf(ctx.read("$.glb_bts", Object.class))));

    }

    @Test
    public void testGlbD() {
        String json = "{\"glb_dc\":\"1301\",\"glb_cl\":\"https://m.gearbest.com/promotion-Lucky-Wheel-special-4508.html?lkid=17051471\",\"glb_w\":\"538\",\"glb_t\":\"ie\",\"glb_s\":\"b03\",\"glb_p\":\"4508\",\"glb_osr\":\"ol=http://syndication.exdynsrv.com/cimp.php|href=https://m.gearbest.com/promotion-Lucky-Wheel-special-4508.html?lkid=17051471\",\"glb_od\":\"gpnfczdofeqm1542386426146\",\"glb_plf\":\"m\",\"glb_tm\":\"1542699333390\",\"glb_d\":\"10002\",\"glb_pl\":\"http://syndication.exdynsrv.com/cimp.php?data=TVRVME1qWTVPVE16TVh3NE5UZ3lNR1V3Tmpoa01XRmxPVEJsTUROa01qZ3lNV1ZpWmpWbU1URXpOUT09fGh0dHBzOi8vZ2VhcmJlc3QubW9iaWxldHJhY2tpbmcucnUvU0dQfGh0dHB8MTgzLjkwLjM3LjE5M3xTR1B8MTQwfGFkZXhjaGFuZ2UtNzM5MjQwLmNvbXwyNTQ3NzR8NTU1MjA0fDczOTI0MHwzMDU1NDk2fDUxM3wyNDgxMDQ4fDIxNDQyNTA4fDExfDEwMHw1NnwxNjY4fDM0MjU1NzQ3fDExNzA4MnwwLjF8NzB8VVNEfFVTRHwxfDF8MjJ8fDF8U0dQfHw3NHw0fDB8fHZpc2l0b3JfaWR8YTFhOGJiYTBmMzQ0NzlkZGQwYmFkNDk3NzlkYTE1N2J8MXwwfDExNzA4Mi5vcGVudHJhZmYuY29tfDB8MHwwfDB8MXwyfGV4Y2hhbmdlX2xpbmt8MmJlZDNkMDYzNzJlOTA1ODhhMWE0Yjc3ZTI4Y2UyMjd8MHwwfDB8NzUzNTk1NHwtMXwwfDE4ODAyNTJ8fHwxfDE0NDB8fDB8T0t8ZTY5NjUyMTcwZTY3NTA1ODU2NjliYWJmYzgxNmIzYmQ=\",\"glb_b\":\"b\",\"glb_oi\":\"ce23c6b8214bfe7d60d2a99cede33e6f\",\"timestamp\":1542699334000}";
        ReadContext ctx = JsonPath.parse(json);
        Object filterValue = ctx.read("$.glb_d");
        System.out.println(filterValue);

    }

    private Map<String, Object> finalJsonMap(String source) {
        Map<String, Object> sourceMap = NginxLogConvertUtil.getNginxLogParameters(source);;
        Map<String,Object> finalMap = new HashMap<>();
        sourceMap.entrySet().stream().forEach(e -> {
            String value = String.valueOf(e.getValue());
            if (value.startsWith("{")) {
                try {
                    if (!value.contains(":")) {
                        //logger.warn("{} 报表异常埋点数据 {}", this.reportBuildRule.getReportName(), value);
                    }else {
                        finalMap.put(e.getKey(), JacksonUtil.readValue(value, Map.class));
                    }
                } catch (Exception e1) {
                    //logger.error("{} 报表埋点字段 map 转换失败source: {} map: {}", this.reportBuildRule.getReportName(), source, value, e);
                }
            } else if (value.startsWith("[") && !value.startsWith("[ETA]") && value.endsWith("]")) {
                try {
                    finalMap.put(e.getKey(), JacksonUtil.readValue(value, List.class));
                } catch (Exception e1) {
                    //logger.error("{} 报表埋点字段 list 转换失败： {}", this.reportBuildRule.getReportName(), value, e);
                }
            }else {
                finalMap.put(e.getKey(), value);
            }
        });
        return finalMap;
    }

}
