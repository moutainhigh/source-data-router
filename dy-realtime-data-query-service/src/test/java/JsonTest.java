import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalegrow.dy.dto.UserActionEsDto;
import io.searchbox.client.config.HttpClientConfig;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTest {
    public static ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void stringTest() {
        String appIndexPrefix = "dy_app_&&_event";
        System.out.println(appIndexPrefix.replace("&&", "gb"));
        System.out.println(appIndexPrefix);
    }


    @Test
    public void test() throws Exception {
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        String s = "\"{\"site\":\"zaful\",\"device_id\":\"wangzhongfu\",\"user_id\":\"0\",\"event_name\":\"af_impression\",\"event_value\":\"232052511\",\"platform\":\"android\",\"timestamp\":1540445344000}\"";
        if (s.startsWith("\"")) {
           s= s.replaceFirst("\"", "");
        }
        if (s.endsWith("\"")) {
            s=s.substring(0, s.lastIndexOf("\""));
        }
        System.out.println(objectMapper.readValue(s, UserActionEsDto.class));
    }

    protected HttpClientConfig createHttpClientConfig() {
        HttpClientConfig.Builder builder = new HttpClientConfig.Builder(
                "");
        builder.maxTotalConnection(500);
        builder.defaultMaxTotalConnectionPerRoute(250);
        return builder.build();
    }

    @Test
    public void test2() throws UnsupportedEncodingException {
        String s = new String("{\"af_content_id\":\"282870904,221327810,282828203,281489502,237481404,280738409,279319303,281933301,281928804,233750902,283581202,282228504,233979204,220356108,280549620,281937902,281179903,232984403,282724002,281493001\",\"af_inner_mediasource\":\"category_jackets-coats\\\\\\\\u00253Ddeeplink\\,\\extras\\:{\\fb_app_id\\:1396335280417835}}\"}".getBytes(), "utf-8");
        System.out.println(s);
    }

    @Test
    public void testStream() {
        Map<String, List<String>> map = new HashMap<>();
        List<String> ab = Arrays.asList(new String[]{"a", "b"});
        List<String> ac = Arrays.asList(new String[]{"a", "c"});
        List<String> ad = Arrays.asList(new String[]{"a", "d"});
        List<String> bc = Arrays.asList(new String[]{"b", "c"});

        map.put("1", ab);
        map.put("2", ac);
        map.put("3", ad);
        map.put("4", bc);

       // List<String> strings = map.entrySet().stream()
    }

}
