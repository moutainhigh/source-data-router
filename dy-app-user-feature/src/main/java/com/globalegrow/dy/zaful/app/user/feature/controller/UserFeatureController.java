package com.globalegrow.dy.zaful.app.user.feature.controller;

import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.ZafulAppUserFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dy/zaful")
public class UserFeatureController {

    @Autowired
    private ZafulAppUserFeatureService zafulAppUserFeatureService;

    @RequestMapping("user/app/feature")
    public AppUserFeatureResponse getUserFeature(AppUserFeatureRequest request) {

        return null;
    }

}
