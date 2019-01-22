package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.SearchWordSkusRequest;
import com.globalegrow.dy.dto.SearchWordSkusResponse;
import com.globalegrow.dy.dto.UserActionData;
import com.globalegrow.dy.service.SearchWordSkusService;
import com.globalegrow.dy.utils.MD5CipherUtil;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import io.searchbox.params.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchWordSkusServiceEsImpl implements SearchWordSkusService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    //@Qualifier("myJestClient")
    private JestClient jestClient;

    private String searchWordIndex = "dy_&&_search_words_skus_rel";

    @Override
    public SearchWordSkusResponse getSkusByWord(SearchWordSkusRequest request) {
        SearchWordSkusResponse response = new SearchWordSkusResponse();

        String esIndex = this.searchWordIndex.replace("&&", request.getSite().toLowerCase());
        String id = MD5CipherUtil.generatePassword(request.getWord());
        Get get = new Get.Builder(esIndex, id).type("log").setParameter(Parameters.ROUTING, id).build();

        try {
            JestResult jestResult = this.jestClient.execute(get);
            if (jestResult != null) {

                Map<String, Object> map = jestResult.getSourceAsObject(Map.class);


                // UserRealtimeEventActions realtimeEventActions = jestResult.getSourceAsObject(UserRealtimeEventActions.class);
                if (map != null) {

                    List<String> skus = (List<String>) map.get("skus");

                    if (skus != null && skus.size() > 0) {

                        response.setData(skus);

                    }

                }

            }

        } catch (Exception e) {
            logger.error("搜索词数据 query es error ,params: {}", get.getURI(), e);
        }

        return response;
    }
}
