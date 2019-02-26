package com.globalegrow.hdfs.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Component
public class HdfsUtil {

    private static String clusterName = "glbgnameservice";
    private static final String HADOOP_URL = "hdfs://"+clusterName;

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
            hahdfsServer = "hdfs://" + address.getHostAddress() + ":" + active.getPort();
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

}
