package com.globalegrow.dy.es;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesAction;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Repository
@Data
@Slf4j
public class ElasticSearchRepository {

    @Autowired
    private TransportClient client;


    public IndicesAliasesResponse indexAddAlias(String indexName, String indexAlias) throws IOException, ExecutionException, InterruptedException {
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        indicesAliasesRequest.addAliasAction(IndicesAliasesRequest.AliasActions.add().index(indexName).alias(indexAlias));
        return this.client.admin().indices().aliases(indicesAliasesRequest).get();
    }


    public CreateIndexResponse createIndexWithoutAlias(String indexName, Integer shards, Integer replicas) {
        return this.client.admin().indices().prepareCreate(indexName).setSettings(Settings.builder()
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)).get();

    }


    public CreateIndexResponse createIndex(String indexName, String indexAlias, Integer shards, Integer replicas) {

        return this.client.admin().indices().prepareCreate(indexName).setSettings(Settings.builder()
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)).addAlias(new Alias(indexAlias)).get();

    }

    public DeleteIndexResponse deleteIndex(String indexName) {
        return this.client.admin().indices().prepareDelete(indexName).get();
    }

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

    public List<Map<String, Object>> idInSearch(String index, String type, List<String> ids) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Arrays.stream(this.client.prepareSearch(index).setTypes(type).setSize(ids.size())
                .setQuery(QueryBuilders.termsQuery("_id", ids)).get().getHits().getHits()).forEach(searchHitFields -> mapList.add(searchHitFields.getSourceAsMap()));
        return mapList;
    }

}
