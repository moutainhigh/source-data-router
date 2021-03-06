package com.globalegrow.dy.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.globalegrow.dy.service.SearchWordSkusService;
import com.globalegrow.dy.service.UserActionQueryAllService;
import com.globalegrow.dy.service.UserBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("user")
@Slf4j
public class UserActionController {

    @Autowired
    @Qualifier("realTimeUserActionRedisServiceImpl")
    private RealTimeUserActionService realTimeUserActionServiceImpl;

    @Autowired
    @Qualifier("realTimeUserActionEsServiceImpl")
    private RealTimeUserActionService realTimeUserActionEsServiceImpl;

    @Autowired
    private UserBaseInfoService userBaseInfoService;

    @Autowired
    private UserActionQueryAllService userActionQueryAllService;

    @Autowired
    private SearchWordSkusService searchWordSkusService;

    @Value("${query-realtime-data-from-es:false}")
    private Boolean queryRealtimeDataFromEs = false;

    @PostConstruct
    public void init() {
        // ????????????????????????????????????
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource("user_realtime_1000_events");
        // set limit qps to 20
        rule1.setCount(5000);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rules.add(rule1);
        FlowRuleManager.loadRules(rules);

        // ????????????????????????????????????
        List<DegradeRule> rulesD = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("user_realtime_1000_events");
        // set threshold RT, 50 ms
        rule.setCount(50);
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        rule.setTimeWindow(10);
        rulesD.add(rule);
        DegradeRuleManager.loadRules(rulesD);
    }

    /**
     * ????????????????????????
     *
     * @param request
     * @return
     */
    @SentinelResource(value = "user_base", blockHandler = "userBaseInfoExceptionHandler")
    @RequestMapping(value = "base", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserBaseInfoResponse getUsersBaseInfo(@Validated @RequestBody UserBaseInfoRequest request) {
        return this.userBaseInfoService.getUsersBaseInfo(request);
    }

    public UserBaseInfoResponse userBaseInfoExceptionHandler(UserBaseInfoRequest request) {
        UserBaseInfoResponse response = new UserBaseInfoResponse();
        response.setSuccess(false);
        response.setMessage("????????????");
        return response;
    }

    /**
     * ??????????????????????????????
     *
     * @param request
     * @return
     */
    @SentinelResource(value = "user_all_event", blockHandler = "userAllEventBlockHandler")
    @RequestMapping(value = "all", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionQueryAllResponse getAllUserActions(@Validated @RequestBody UserActionQueryAllRequest request) {
        if (StringUtils.isNotBlank(request.getUserId()) ) {
            int length = request.getUserId().split(",").length;
            if (length > 100) {
                UserActionQueryAllResponse userActionQueryAllResponse=new UserActionQueryAllResponse();
                userActionQueryAllResponse.setMessage("userId is to much");
                userActionQueryAllResponse.setSuccess(false);
                return userActionQueryAllResponse;
            }
        }



        return this.userActionQueryAllService.getAllUserActions(request);
    }

    public UserActionQueryAllResponse userAllEventBlockHandler(UserActionQueryAllRequest request) {
        UserActionQueryAllResponse response = new UserActionQueryAllResponse();
        response.setSuccess(false);
        response.setMessage("????????????");
        return response;
    }

    /**
     * ???????????? sku ??????????????????
     *
     * @param request
     * @return
     */
    @SentinelResource(value = "search_word_skus", blockHandler = "searchBlockHandler")
    @RequestMapping(value = "search/word/skus", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public SearchWordSkusResponse searchWordSkus(@Validated @RequestBody SearchWordSkusRequest request) {
        return this.searchWordSkusService.getSkusByWord(request);
    }

    public SearchWordSkusResponse searchBlockHandler(SearchWordSkusRequest request) {
        SearchWordSkusResponse response = new SearchWordSkusResponse();
        response.setSuccess(false);
        response.setMessage("????????????");
        return response;
    }

    /**
     * ????????????????????????
     *
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @SentinelResource(value = "user_realtime_1000_events", blockHandler = "exceptionHandler", fallback = "fallBackExceptionHandler")
    @RequestMapping(value = "getUserInfo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userActionInfo(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException, ParseException {
        long start = System.currentTimeMillis();
        UserActionResponseDto responseDto;
        if (this.queryRealtimeDataFromEs) {
            responseDto = this.realTimeUserActionEsServiceImpl.getActionByUserDeviceId(parameterDto);
        }else {
            responseDto = this.realTimeUserActionServiceImpl.getActionByUserDeviceId(parameterDto);
        }

        /*initFlowRules();
        UserActionResponseDto responseDto = new UserActionResponseDto();
        Entry entry = null;
        try {
            entry = SphU.entry("getUserInfo");
            responseDto = this.realTimeUserActionEsServiceImpl.userActionData(parameterDto);
        } catch (BlockException e1) {
            responseDto.setMessage("?????????????????????");
            responseDto.setSuccess(false);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }*/
        log.info("?????????: {}", System.currentTimeMillis() - start);
        return responseDto;
    }

    public UserActionResponseDto fallbackMethod(UserActionParameterDto userActionParameterDto) {
        log.warn("?????????????????????");
        UserActionResponseDto actionResponseDto = new UserActionResponseDto();
        actionResponseDto.setMessage("?????????????????????");
        actionResponseDto.setSuccess(false);
        return actionResponseDto;
    }


    @ExceptionHandler({Exception.class})
    public UserActionResponseDto databaseError(HttpServletRequest req, Exception e) {
        log.error("????????????????????????" + req.getRequestURI(), e);
        UserActionResponseDto responseDto = new UserActionResponseDto();
        //ReponseDTO<Object> reponseDTO = new ReponseDTO<>(ExceptionCode.FAIL.getCode(),ExceptionCode.FAIL.getMessage());
        responseDto.setSuccess(false);
        StringBuilder stringBuilder = new StringBuilder();
        if (e instanceof BindException) {
            ((BindException) e).getFieldErrors().forEach(fieldError -> stringBuilder.append(fieldError.getDefaultMessage()));
        }
        if (e instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().forEach(error -> stringBuilder.append(error.getDefaultMessage() + "\n"));
        }
        responseDto.setMessage(stringBuilder.toString());
        return responseDto;
    }

    public Boolean getQueryRealtimeDataFromEs() {
        return queryRealtimeDataFromEs;
    }

    public void setQueryRealtimeDataFromEs(Boolean queryRealtimeDataFromEs) {
        this.queryRealtimeDataFromEs = queryRealtimeDataFromEs;
    }

    public UserActionResponseDto exceptionHandler(UserActionParameterDto parameterDto, BlockException ex) {
        log.warn("redis ????????????????????? es " + parameterDto.toString());
        return this.realTimeUserActionEsServiceImpl.getActionByUserDeviceId(parameterDto);
    }

    public UserActionResponseDto fallBackExceptionHandler(UserActionParameterDto parameterDto) {
        log.warn("????????????????????? es " + parameterDto.toString());
        return this.realTimeUserActionEsServiceImpl.getActionByUserDeviceId(parameterDto);
    }
}
