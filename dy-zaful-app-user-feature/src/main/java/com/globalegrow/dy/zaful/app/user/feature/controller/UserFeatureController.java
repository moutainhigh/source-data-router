package com.globalegrow.dy.zaful.app.user.feature.controller;

import com.globalegrow.dy.zaful.app.user.feature.dto.ZafulAppUserFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.ZafulAppUserFeatureResponse;
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
    public ZafulAppUserFeatureResponse getUserFeature(ZafulAppUserFeatureRequest request) {

        return null;
    }

}
