package com.globalegrow.fixed.consumer;

import com.globalegrow.fixed.queen.AbstractFlinkJobQueen;
import com.globalegrow.fixed.queen.FBADFeatureMessage;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.concurrent.DelayQueue;

@Data
@Slf4j
@RequiredArgsConstructor
public class DelayQueenConsumer implements Runnable {

    @NonNull
    private DelayQueue<AbstractFlinkJobQueen> flinkJobQueens;

    @Override
    public void run() {
        while (true) {
            try {
                AbstractFlinkJobQueen flinkJobQueen = this.flinkJobQueens.take();
                if (flinkJobQueen != null) {

                    if (flinkJobQueen.canRun()) {

                        log.info("{} 可运行", flinkJobQueen);
                        flinkJobQueen.runFlinkJob();

                    }else {
                        log.info("{} 不可运行，重新放入延时队列", flinkJobQueen);
                        flinkJobQueens.offer(flinkJobQueen);

                    }

                }
            } catch (InterruptedException e) {
                log.error("读取延时队列出错", e);
            }
        }
    }
}
