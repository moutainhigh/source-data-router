package com.globalegrow;

import com.globalegrow.common.hbase.CommonHbaseMapper;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.globalegrow"},exclude={DataSourceAutoConfiguration.class})
@Configuration
@ConfigurationProperties(prefix = "spring.hbase")
@RestController
public class ServiceStart {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStart.class, args);
    }

    @Value("${spring.hbase.zk-servers}")
    private String zkServers;

    @Bean
    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        // System.out.println("++++d+++++++"+this.getZkServers());
        conf.set("hbase.zookeeper.quorum", this.getZkServers());
        conf.setInt("hbase.rpc.timeout",20000);
        conf.setInt("hbase.org.snailgary.demo.websocket.undertow.client.operation.timeout",30000);
        conf.setInt("hbase.org.snailgary.demo.websocket.undertow.client.scanner.timeout.period",20000);
        return conf;
    }

    @Autowired
    private CommonHbaseMapper commonHbaseMapper;

    @RequestMapping("huserid")
    public Object getUserId(String cookie) {
        return this.commonHbaseMapper.selectRowKeyFamilyColumn("dy_cookie_userid_rel", cookie, "user_id", "cookie_userid");
    }

    /*@RequestMapping("huserid0")
    public Object getUserId0(String cookie) {
        this.commonHbaseMapper.
        return this.commonHbaseMapper.selectRowKeyFamilyColumn("dy_cookie_userid_rel", cookie, "user_id", "cookie_userid");
    }*/


    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

}
