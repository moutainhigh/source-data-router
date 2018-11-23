import com.globalegrow.util.GsonUtil;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.util.Map;

public class JsonTest {

    @Test
    public void test() {
        String s = "{\"site\":\"zaful\",\"device_id\":\"wangzhongfu\",\"user_id\":\"0\",\"event_name\":\"af_impression\",\"event_value\":\"232052511\",\"platform\":\"android\",\"timestamp\":1540445344000}";
        System.out.println(GsonUtil.readValue(s.replaceAll("\\\\", ""), Map.class));
    }

    @Test
    public void testEs() {
       /* QueryBuilder qb = termQuery("multi", "test");

        SearchResponse scrollResp = client.prepareSearch(test)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(100).get(); //max of 100 hits will be returned for each scroll
//Scroll until no hits are returned
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                //Handle the hit...
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        } while(scrollResp.getHits().getHits().length != 0);*/
    }

}
