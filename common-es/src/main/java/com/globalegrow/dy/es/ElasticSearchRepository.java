package com.globalegrow.dy.es;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ElasticSearchRepository {

    @Autowired
    private TransportClient client;

    public IndexResponse addIndex(String index, String type, String id, Map<String, Object> data) {
        return this.client.prepareIndex(index, type, id).setSource(data).execute().actionGet();
    }

    public UpdateResponse updateIndex(String index, String type, String id, Map<String, Object> data){
        return this.client.prepareUpdate(index, type, id).setDoc(data).execute().actionGet();
    }

    public Map<String, Object> getDoc(String index, String type, String id){
        return getResponse(index, type, id).getSourceAsMap();
    }

    public GetResponse getResponse(String index, String type, String id){
        return this.client.prepareGet(index, type, id)
                .execute()
                .actionGet();
    }

    public DeleteResponse deleteDoc(String index, String type, String id) {
       return this.client.prepareDelete(index, type, id)
                .execute()
                .actionGet();
    }



}
