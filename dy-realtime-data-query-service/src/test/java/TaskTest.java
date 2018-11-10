import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskTest {


    @Test
    public void listTest() {
        //List
    }

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void test() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务执行");
            }
        });
        while (true) {

        }
    }

}
