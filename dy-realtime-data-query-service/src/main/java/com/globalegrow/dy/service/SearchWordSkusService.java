package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.SearchWordSkusRequest;
import com.globalegrow.dy.dto.SearchWordSkusResponse;

public interface SearchWordSkusService {

    SearchWordSkusResponse getSkusByWord(SearchWordSkusRequest request);

}
