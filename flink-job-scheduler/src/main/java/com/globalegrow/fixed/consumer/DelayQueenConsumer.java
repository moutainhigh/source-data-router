package com.globalegrow.fixed.consumer;

import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.DelayQueue;

@Data
@Slf4j
@RequiredArgsConstructor
@Deprecated

public class DelayQueenConsumer implements Runnable {

    @NonNull
    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    @Override
    public void run() {
        while (true) {
            try {
                DyHdfsCheckExistsJobMessage flinkJobQueen = this.flinkJobQueens.take();
                if (flinkJobQueen != null) {

                    if (flinkJobQueen.canRun()) {

                        log.info("{} 可运行", flinkJobQueen);
                        flinkJobQueen.runFlinkJob();

                    }else {
                        log.info("{} 不可运行，重新放入延时队列", flinkJobQueen);
                        DyHdfsCheckExistsJobMessage dyHdfsCheckExistsJobMessage = new DyHdfsCheckExistsJobMessage(flinkJobQueen.getHdfsPath(), System.currentTimeMillis(), 300000L, flinkJobQueen.getFlinkJobCommandLine());
                        flinkJobQueens.offer(dyHdfsCheckExistsJobMessage);

                    }

                }
            } catch (InterruptedException e) {
                log.error("读取延时队列出错", e);
            }
        }
    }
}
