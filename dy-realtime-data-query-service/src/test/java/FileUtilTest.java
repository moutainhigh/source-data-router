import com.globalegrow.dy.utils.FileUtil;
import org.junit.Test;

public class FileUtilTest {

    @Test
    public void test() {
        System.out.println(FileUtil.getListFiles("E:\\浏览器下载").size());
    }

}
