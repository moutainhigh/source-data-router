package com.globalegrow.cu.config;

//@Configuration
//@ConfigurationProperties(prefix = "spring.hbase")
//public class HbaseConfig {
//
//    private String zkServers;
//
//    @Bean
//    public org.apache.hadoop.conf.Configuration configuration() {
//        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
//        System.out.println("++++d+++++++"+this.getZkServers());
//        conf.set("hbase.zookeeper.quorum", this.getZkServers());
//        conf.setInt("hbase.rpc.timeout",20000);
//        conf.setInt("hbase.org.snailgary.demo.websocket.undertow.client.operation.timeout",30000);
//        conf.setInt("hbase.org.snailgary.demo.websocket.undertow.client.scanner.timeout.period",20000);
//        return conf;
//    }
//
//
//    public String getZkServers() {
//        return zkServers;
//    }
//
//    public void setZkServers(String zkServers) {
//        this.zkServers = zkServers;
//    }
//}
