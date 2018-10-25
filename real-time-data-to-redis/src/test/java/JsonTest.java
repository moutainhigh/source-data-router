import com.globalegrow.util.GsonUtil;
import org.junit.Test;

import java.util.Map;

public class JsonTest {

    @Test
    public void test() {
        String s = "{\"site\":\"zaful\",\"device_id\":\"wangzhongfu\",\"user_id\":\"0\",\"event_name\":\"af_impression\",\"event_value\":\"232052511\",\"platform\":\"android\",\"timestamp\":1540445344000}";
        System.out.println(GsonUtil.readValue(s.replaceAll("\\\\", ""), Map.class));
    }

}
