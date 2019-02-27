package com.globalegrow.dy.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class EsConfig {

    @Value("${app.es.socket.address}")
    private String esServers;

    @Value("${app.es.cluster-name}")
    private String esClusterName;

    @Bean
    public TransportClient transportClient() {
        TransportClient client = null;

        List<InetSocketTransportAddress> transports = new ArrayList<>();
        for (String s : this.esServers.split(",")) {
            try {
                transports.add(new InetSocketTransportAddress(InetAddress.getByName(s.split(":")[0]), Integer.valueOf(s.split(":")[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Settings.Builder builder = Settings.builder();
        builder.put("cluster.name", this.esClusterName).put("client.transport.sniff", true)
       /* .put("transport.type","netty4")
                .put("http.type", "netty4")*/;
        client = new PreBuiltTransportClient(builder.build());
        TransportClient finalClient = client;
        transports.forEach(transport -> finalClient.addTransportAddress(transport));

        return finalClient;
    }

}
