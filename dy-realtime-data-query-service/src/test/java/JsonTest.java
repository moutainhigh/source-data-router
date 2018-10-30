import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalegrow.dy.dto.UserActionEsDto;
import com.globalegrow.dy.utils.JacksonUtil;
import com.globalegrow.util.GsonUtil;
import com.google.gson.Gson;
import io.searchbox.client.config.HttpClientConfig;
import org.apache.http.HttpHost;
import org.junit.Test;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class JsonTest {
    public static ObjectMapper objectMapper = new ObjectMapper();


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

}
