package com.globalegrow.dy.zaful.app.user.feature.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.globalegrow.dy.controller.CommonController;
import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.FBADUserFeatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "facebook 广告特征数据接口")
@RestController
@RequestMapping("fb-ad-user-feature")
@Data
@Slf4j
public class FBADUserFeatureController extends CommonController {

    @Autowired
    private FBADUserFeatureService fbadUserFeatureService;

    @PostConstruct
    public void init() {
        // Facebook 广告用户特征接口
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource("fb_ad_user_feature");
        // set limit qps to 20
        rule1.setCount(5000);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rules.add(rule1);

        //user_base_info_fbad_feature

        FlowRule rule2 = new FlowRule();
        rule2.setResource("user_base_info_fbad_feature");
        // set limit qps to 20
        rule2.setCount(5000);
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule2.setLimitApp("default");
        rules.add(rule2);

        //zaful_app_user_layer
        FlowRule rule3 = new FlowRule();
        rule3.setResource("zaful_app_user_layer");
        // set limit qps to 20
        rule3.setCount(5000);
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule3.setLimitApp("default");
        rules.add(rule3);

        FlowRuleManager.loadRules(rules);

    }

    /**
     * Facebook 广告用户特征
     * @param request
     * @return
     */
    @ApiOperation(value = "根据 facebook 广告 id 获取广告特征")
    @SentinelResource(value = "fb_ad_user_feature", blockHandler = "failed", fallback = "failed")
    @PostMapping(produces = "application/json;charset=UTF-8")
    public FbADFeatureResponse getAdUserFeatureDataById(@Validated @RequestBody FbADFeatureRequest request){
        return this.fbadUserFeatureService.getAdUserFeatureDataById(request);
    }

    public FbADFeatureResponse failed() {
        FbADFeatureResponse response = new FbADFeatureResponse();
        response.setSuccess(false);
        return response;
    }

}
