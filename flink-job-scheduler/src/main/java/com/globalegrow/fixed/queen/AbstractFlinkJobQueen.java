package com.globalegrow.fixed.queen;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
@ToString
public abstract class AbstractFlinkJobQueen implements Delayed {

    protected Long id;
    protected long executeTime;//执行时间

    protected String flinkJobCommandLine;

    /**
     * 是否满足可过滤条件，如 hdfs 文件是否存在等等
     * @return
     */
     public boolean canRun(){
         return true;
     }

    public boolean runFlinkJob() {
         log.info("job rerun");
        if (this.canRun()) {
            Process process = null;
            //List<String> processList = new ArrayList<String>();
            try {
                process = Runtime.getRuntime().exec(this.getFlinkJobCommandLine());
                try(BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                    String line = "";
                    while ((line = input.readLine()) != null) {
                        //processList.add(line);
                        log.info(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return  unit.convert(this.executeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        AbstractFlinkJobQueen message = (AbstractFlinkJobQueen) o;
        return message.getId().compareTo(this.getId());
    }

}
