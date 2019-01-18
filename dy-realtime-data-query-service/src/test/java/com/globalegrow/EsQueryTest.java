package com.globalegrow;

import com.globalegrow.dy.dto.UserActionData;
import com.globalegrow.dy.utils.JacksonUtil;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.params.Parameters;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsQueryTest {

    @Autowired
    //@Qualifier("myJestClient")
    private JestClient jestClient;

    //dy_app_gb_event_realtime/log/1539374829411-5174249294348526115af_view_product
    @Test
    public void testGet() throws IOException {
        Get get = new Get.Builder("dy_app_gb_event_realtime", "1539374829411-5174249294348526115af_view_product").type("log").setParameter(Parameters.ROUTING, "1539374829411-5174249294348526115af_view_product")
                .build();
        JestResult jestResult = this.jestClient.execute(get);
        Map<String, Object> map = jestResult.getSourceAsObject(Map.class);
        System.out.println(map.get("skus").getClass());

        List<String> skus = (List<String>) map.get("skus");
        List<UserActionData> list = new ArrayList<>();

        skus.forEach(value -> list.add(new UserActionData(value.substring(0, value.lastIndexOf("_")), Long.valueOf(value.substring(value.lastIndexOf("_") + 1)))));

        System.out.println(list.size());
    }

    @Test
    public void test() throws Exception {
        //1539374829411-5174249294348526115
        String indexName = "dy_app_gb_user_base";
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(1000);

        QueryBuilder device_id = QueryBuilders.termsQuery("device_id.keyword", "1539374829411-5174249294348526115-wer");
        queryBuilder.filter(device_id);

        SortBuilder sortBuilder = new FieldSortBuilder("_doc");
        //sortBuilder.order(SortOrder.DESC);

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.sort(sortBuilder);
        Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
        builder.addIndex(indexName);
        Search search = builder
                .addType("log").setParameter(Parameters.SCROLL, "1m")
                .build();
        JestResult jestResult = jestClient.execute(search);





        System.out.println( JacksonUtil.toJSon(((SearchResult) jestResult).getHits(Map.class)));
        JsonObject jsonObject =  jestResult.getJsonObject();
        System.out.println(jsonObject);
        System.out.println(jsonObject.get("hits").getAsJsonObject().get("total").getAsLong());
        System.out.println(jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray().size());
        System.out.println(jestResult.getJsonObject().get("_scroll_id").getAsString());
        //System.out.println(jestResult.getJsonObject().get("total").getAsString());

    }

}
