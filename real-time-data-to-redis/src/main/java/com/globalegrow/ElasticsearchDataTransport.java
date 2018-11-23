package com.globalegrow;

import com.globalegrow.util.NginxLogConvertUtil;
import io.searchbox.client.JestClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.ElasticsearchGenerationException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Component
public class ElasticsearchDataTransport {


    @Autowired
    @Qualifier("myJestClient")
    private JestClient jestClient;

    private TransportClient client;

    private TransportClient clientDataToWrite;



    private boolean run = true;

    private String scrollId = "";

    @PostConstruct
    public void before() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name","esearch-ai-aws").build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("172.31.51.179"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("172.31.51.59"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("172.31.51.250"), 9300));

        Settings settings2 = Settings.builder().put("cluster.name","esearch-ips").build();
        clientDataToWrite = new PreBuiltTransportClient(settings2)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("10.4.4.87"), 9302))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("10.4.4.88"), 9302))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("10.4.4.89"), 9302));


        new Thread(() -> {
            SearchRequest searchRequest = new SearchRequest("posts");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(matchAllQuery());
            searchSourceBuilder.size(20000);
            searchRequest.source(searchSourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            SearchResponse searchResponse = (SearchResponse) client.search(searchRequest);

            do{
                try {
                    BulkRequest request = new BulkRequest();
                    for (SearchHit hit : searchResponse.getHits().getHits()) {
                       Map<String,Object> event = hit.getSourceAsMap();
                        request.add(new IndexRequest("dy-realtime-user-event-sequence",  String.valueOf(event.get("device_id"))).routing(String.valueOf(event.get("device_id")) + String.valueOf(event.get("event_name")))
                                .source(event));
                    }
                    clientDataToWrite.bulk(request);

                    searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(TimeValue.timeValueMinutes(1L)).execute().actionGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }while (searchResponse.getHits().getHits().length != 0);


        }).run();


    }



}
