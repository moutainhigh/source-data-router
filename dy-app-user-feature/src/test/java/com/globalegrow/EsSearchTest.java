package com.globalegrow;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsSearchTest {

    @Autowired
    private TransportClient client;

    @Test
    public void testSearch() {
        SearchResponse response = this.client.prepareSearch("dy_app_zaful_user_feature").setTypes("user_feature")
                .setQuery(QueryBuilders.termsQuery("_id", "1524046698675-1636828","1519343258579-1080520")).get();

        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

}
