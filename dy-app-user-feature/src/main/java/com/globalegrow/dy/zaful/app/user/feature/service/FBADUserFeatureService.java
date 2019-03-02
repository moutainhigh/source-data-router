package com.globalegrow.dy.zaful.app.user.feature.service;

import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureResponse;

public interface FBADUserFeatureService {

    FbADFeatureResponse getAdUserFeatureDataById(FbADFeatureRequest request);

}
