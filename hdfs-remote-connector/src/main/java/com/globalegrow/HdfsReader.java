package com.globalegrow;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

@Component
public class HdfsReader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void before() throws IOException {

        String HDFS_PATH = "hdfs://172.31.57.86:8020"; //要连接的hadoop
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", HDFS_PATH);
        //连接文件系统,FileSystem用来查看文件信息和创建文件
        FileSystem fileSystem = FileSystem.get(configuration);

        //操作文件io,用来读写
        configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());  //设置处理分布式文件系统
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory(configuration));
        //FsUrlStreamHandlerFactory()  不加参数连接会无法识别的hdfs协议,原因是hadoop在获取处理hdfs协议的控制器时获取了configuration的fs.hdfs.impl值

        //获取Hadoop文件
        InputStream inputStream = new URL(HDFS_PATH + "/user/wangzhongfu/2018-10-23").openStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while(reader.ready()) {
            String line = reader.readLine();
            logger.info("hdfs 文件数据  {}",line);
        }


    }

}
