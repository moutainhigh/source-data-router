package com.globalegrow.dy.zaful.app.user.feature.service;

import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureResponse;

public interface AppUserFeatureService {

    AppUserFeatureResponse appUserFeatures(AppUserFeatureRequest request);

}
