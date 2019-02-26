package com.globalegrow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HdfsConfig {

    private static String clusterName = "glbgnameservice";
    private static final String HADOOP_URL = "hdfs://" + clusterName;

    @Bean
    public org.apache.hadoop.conf.Configuration dyConfiguration() {
        org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
        //config.set("fs.defaultFS", hdfsServer);
        config.set("fs.defaultFS", HADOOP_URL);
        config.set("dfs.nameservices", clusterName);
        config.set("dfs.ha.namenodes." + clusterName, "nn1,nn2");
        // 高可用的配置：当其中一个变成standy时，打印异常，并自动切换到另一个namedata去取数据
        config.set("dfs.namenode.rpc-address." + clusterName + ".nn1", "bts-master:8020");
        config.set("dfs.namenode.rpc-address." + clusterName + ".nn2", "bts-masterbak:8020");
        //conf.setBoolean(name, value);
        config.set("dfs.client.failover.proxy.provider." + clusterName,
                "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        return config;
    }

    @Bean
    public org.apache.hadoop.conf.Configuration bigDataConfiguration() {
        org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
        //config.set("fs.defaultFS", hdfsServer);
        config.set("fs.defaultFS", HADOOP_URL);
        config.set("dfs.nameservices", clusterName);
        config.set("dfs.ha.namenodes." + clusterName, "nn1,nn2");
        // 高可用的配置：当其中一个变成standy时，打印异常，并自动切换到另一个namedata去取数据
        config.set("dfs.namenode.rpc-address." + clusterName + ".nn1", "172.31.20.96:8020");
        config.set("dfs.namenode.rpc-address." + clusterName + ".nn2", "172.31.57.86:8020");
        //conf.setBoolean(name, value);
        config.set("dfs.client.failover.proxy.provider." + clusterName,
                "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        return config;
    }
}
