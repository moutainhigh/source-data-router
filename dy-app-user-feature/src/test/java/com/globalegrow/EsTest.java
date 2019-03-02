package com.globalegrow;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class EsTest {


    public TransportClient transportClient() {
        TransportClient client = null;

        List<InetSocketTransportAddress> transports = new ArrayList<>();
        for (String s : "100.26.74.93:9302,100.26.77.0:9302,18.215.206.192:9302".split(",")) {
            try {
                transports.add(new InetSocketTransportAddress(InetAddress.getByName(s.split(":")[0]), Integer.valueOf(s.split(":")[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Settings.Builder builder = Settings.builder();
        builder.put("cluster.name", "esearch-aws-dy")/*.put("client.transport.sniff", true)*/
       /* .put("transport.type","netty4")
                .put("http.type", "netty4")*/;
        client = new PreBuiltTransportClient(builder.build());
        TransportClient finalClient = client;
        transports.forEach(transport -> finalClient.addTransportAddress(transport));

        return finalClient;
    }

    @Test
    public void test() {
        TransportClient client = transportClient();
        client.admin().indices().prepareCreate("wzf_test_index").setSettings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 1)).get();
    }

}
