package com.globalegrow.dy.zaful.app.user.feature.service.impl;

import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.FBADUserFeatureService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
@Slf4j
public class FBADUserFeatureServiceEsImpl implements FBADUserFeatureService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Value("${app.es.fb.index-name:dy_fb_ad_feature}")
    private String fbIndexName;

    @Value("${app.es.fb.index-type:ad_feature}")
    private String fbIndexType;

    @Override
    public FbADFeatureResponse getAdUserFeatureDataById(FbADFeatureRequest request) {
        FbADFeatureResponse response = new FbADFeatureResponse();
        log.debug("广告信息 FB id: {}", request.getFb_adset_id());

        response.setData(this.elasticSearchRepository.getDoc(this.fbIndexName, this.fbIndexType, request.getFb_adset_id()));

        return response;
    }

}
