package com.globalegrow.hdfs.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class HdfsUtil {

    public static Configuration dyConfiguration;
    public static Configuration bigDataConfiguration;

    @Autowired
    public void setDyConfiguration(Configuration dyConfiguration) {
        HdfsUtil.dyConfiguration = dyConfiguration;
    }

    @Autowired
    public void setBigDataConfiguration(Configuration bigDataConfiguration) {
        HdfsUtil.bigDataConfiguration = bigDataConfiguration;
    }

    public static String getDyActiceService() {
        String hahdfsServer = "";

        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(dyConfiguration)) {
            InetSocketAddress active = HAUtil.getAddressOfActive(fileSystem);
            InetAddress address = active.getAddress();
            hahdfsServer = "hdfs://" + address.getHostName() + ":" + active.getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hahdfsServer;
    }


    public static String getBigDataActiveNamenode() {

        String hahdfsServer = "";

        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(bigDataConfiguration)) {
            InetSocketAddress active = HAUtil.getAddressOfActive(fileSystem);
            InetAddress address = active.getAddress();
            hahdfsServer = "hdfs://" + address.getHostAddress() + ":" + active.getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hahdfsServer;
    }

    public static boolean dyFileExist(String filePath) {
        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(dyConfiguration)) {
            return fileSystem.exists(new Path(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean bigDataFileExist(String filePath) {
        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(bigDataConfiguration)) {
            return fileSystem.exists(new Path(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getDyFileContentString(String filePath) {
        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(dyConfiguration)) {
            FSDataInputStream hdfsInStream = fileSystem.open(new Path(filePath));
            InputStreamReader isr = new InputStreamReader(hdfsInStream, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("读取 hdfs 文件失败 {}", filePath, e);
        }
        return "";
    }

    public static void updateDyHdfsFile(String filePath, String content) {
        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(dyConfiguration)) {
            FSDataInputStream hdfsInStream = fileSystem.open(new Path(filePath));
            InputStreamReader isr = new InputStreamReader(hdfsInStream, "utf-8");

            try (FSDataOutputStream hdfsOutStream = fileSystem.create(new Path(filePath))) {
                byte [] str = content.getBytes("UTF-8");
                hdfsOutStream.write(str);
            }

        } catch (IOException e) {
            log.error("读取 hdfs 文件失败 {}", filePath, e);
        }
    }

}
