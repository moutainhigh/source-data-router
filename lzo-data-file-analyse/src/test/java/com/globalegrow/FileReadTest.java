package com.globalegrow;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileReadTest {

    @Test
    public void test() throws IOException {
        File fin = new File("C:\\Users\\wangzhongfu\\Desktop\\test.txt");//读取的文件
        try(LineIterator it = FileUtils.lineIterator(fin, "UTF-8")){
            while (it.hasNext()) {
                String line = it.nextLine();
                System.out.println(line);
                // do something with line
            }
        }

    }


}
