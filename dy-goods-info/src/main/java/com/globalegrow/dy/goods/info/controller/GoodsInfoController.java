package com.globalegrow.dy.goods.info.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.globalegrow.dy.controller.CommonController;
import com.globalegrow.dy.dto.CommonListMapESPageResponse;
import com.globalegrow.dy.dto.CommonListMapResponse;
import com.globalegrow.dy.dto.CommonMapResponse;
import com.globalegrow.dy.goods.info.dto.GoodsBaseInfoRequest;
import com.globalegrow.dy.goods.info.dto.GoodsStatisticsRequest;
import com.globalegrow.dy.goods.info.dto.GoodsTendencyRequest;
import com.globalegrow.dy.goods.info.service.GoodsBaseInfoService;
import com.globalegrow.dy.goods.info.service.GoodsStatisticsInfoService;
import com.globalegrow.dy.goods.info.service.GoodsTendencyService;
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

@Data
@Slf4j
@RestController
@RequestMapping("goods")
public class GoodsInfoController extends CommonController {

    @Autowired
    private GoodsBaseInfoService goodsBaseInfoService;

    @Autowired
    private GoodsStatisticsInfoService goodsStatisticsInfoService;

    @Autowired
    private GoodsTendencyService goodsTendencyService;


    @PostConstruct
    public void init() {
        // Facebook 广告用户特征接口
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource("good_info_statistics");
        // set limit qps to 20
        rule1.setCount(5000);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rules.add(rule1);


        FlowRule rule2 = new FlowRule();
        rule2.setResource("good_info_base_info");
        // set limit qps to 20
        rule2.setCount(5000);
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule2.setLimitApp("default");
        rules.add(rule2);

        // good_info_tendency
        FlowRule rule3 = new FlowRule();
        rule3.setResource("good_info_tendency");
        // set limit qps to 20
        rule3.setCount(5000);
        rule3.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule3.setLimitApp("default");
        rules.add(rule3);

        FlowRuleManager.loadRules(rules);
    }

    @SentinelResource(value = "good_info_statistics", blockHandler = "failed", fallback = "failed")
    @PostMapping(value = "statistics", produces = "application/json;charset=UTF-8")
    public CommonListMapESPageResponse goodsStatisticsInfo(@Validated @RequestBody GoodsStatisticsRequest request){
        if (request.getDimension() == 1 && (request.getDays() == null || request.getDays().size() == 0)) {
            CommonListMapESPageResponse response = new CommonListMapESPageResponse();
            response.setSuccess(false);
            response.setMessage("统计维度为 1 时，查询天为必填");
            return response;
        }
        if (request.getDimension() == 1 && request.getDays().size() > 30) {
            CommonListMapESPageResponse response = new CommonListMapESPageResponse();
            response.setSuccess(false);
            response.setMessage("统计维度为 1 时，查询天最多为 30 天");
            return response;
        }

        return this.goodsStatisticsInfoService.goodsStatisticsInfo(request);
    }

    @SentinelResource(value = "good_info_base_info", blockHandler = "failed", fallback = "failed")
    @PostMapping(value = "base-info", produces = "application/json;charset=UTF-8")
    public CommonListMapResponse goodsBaseInfo(@Validated @RequestBody GoodsBaseInfoRequest request){
        return this.goodsBaseInfoService.goodsBaseInfo(request);
    }

    @SentinelResource(value = "good_info_tendency", blockHandler = "failed", fallback = "failed")
    @PostMapping(value = "tendency", produces = "application/json;charset=UTF-8")
    public CommonListMapResponse goodsTendency(@Validated @RequestBody GoodsTendencyRequest request){
        return this.goodsTendencyService.goodsTendency(request);
    }


    public CommonMapResponse failed() {
        CommonMapResponse response = new CommonMapResponse();
        response.setSuccess(false);
        return response;
    }

}
