package com.globalegrow.dy.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.globalegrow.dy.service.SearchWordSkusService;
import com.globalegrow.dy.service.UserActionQueryAllService;
import com.globalegrow.dy.service.UserBaseInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("user")
public class UserActionController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

    /**
     * 用户基本信息接口
     *
     * @param request
     * @return
     */
    @SentinelResource(value = "user_base", blockHandler = "exceptionHandler")
    @RequestMapping(value = "base", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserBaseInfoResponse getUsersBaseInfo(@Validated @RequestBody UserBaseInfoRequest request) {
        return this.userBaseInfoService.getUsersBaseInfo(request);
    }

    /**
     * 用户全部事件序列接口
     *
     * @param request
     * @return
     */
    @SentinelResource(value = "user_all_event", blockHandler = "exceptionHandler")
    @RequestMapping(value = "all", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionQueryAllResponse getAllUserActions(@Validated @RequestBody UserActionQueryAllRequest request) {
        return this.userActionQueryAllService.getAllUserActions(request);
    }

    /**
     * 搜索词与 sku 对应关系接口
     *
     * @param request
     * @return
     */
    @SentinelResource(value = "search_word_skus", blockHandler = "exceptionHandler")
    @RequestMapping(value = "search/word/skus", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public SearchWordSkusResponse searchWordSkus(@Validated @RequestBody SearchWordSkusRequest request) {
        return this.searchWordSkusService.getSkusByWord(request);
    }

    /**
     * 与源文档保持一致
     *
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @SentinelResource(value = "user_realtime_1000_events", blockHandler = "exceptionHandler", fallback = "exceptionHandler")
    @RequestMapping(value = "getUserInfo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userActionInfo(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException, ParseException {
        long start = System.currentTimeMillis();
        UserActionResponseDto responseDto = this.realTimeUserActionServiceImpl.getActionByUserDeviceId(parameterDto);
        /*initFlowRules();
        UserActionResponseDto responseDto = new UserActionResponseDto();
        Entry entry = null;
        try {
            entry = SphU.entry("getUserInfo");
            responseDto = this.realTimeUserActionEsServiceImpl.userActionData(parameterDto);
        } catch (BlockException e1) {
            responseDto.setMessage("服务超时或繁忙");
            responseDto.setSuccess(false);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }*/
        this.logger.info("总时长: {}", System.currentTimeMillis() - start);
        return responseDto;
    }

    public UserActionResponseDto fallbackMethod(UserActionParameterDto userActionParameterDto) {
        logger.warn("服务超时或繁忙");
        UserActionResponseDto actionResponseDto = new UserActionResponseDto();
        actionResponseDto.setMessage("服务超时或繁忙");
        actionResponseDto.setSuccess(false);
        return actionResponseDto;
    }


    @ExceptionHandler({Exception.class})
    public UserActionResponseDto databaseError(HttpServletRequest req, Exception e) {
        logger.error("调用异常，入口：" + req.getRequestURI(), e);
        UserActionResponseDto responseDto = new UserActionResponseDto();
        //ReponseDTO<Object> reponseDTO = new ReponseDTO<>(ExceptionCode.FAIL.getCode(),ExceptionCode.FAIL.getMessage());
        responseDto.setSuccess(false);
        responseDto.setMessage(e.getMessage());
        return responseDto;
    }

    /*private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule rule = new FlowRule();
        rule.setResource("getUserInfo");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(5000);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }*/

    public UserActionResponseDto exceptionHandler(UserActionParameterDto parameterDto, BlockException ex) {
        logger.warn("redis 查询出错，查询 es " + parameterDto.toString());
//        UserActionResponseDto actionResponseDto = new UserActionResponseDto();
//        actionResponseDto.setMessage("服务超时或繁忙");
//        actionResponseDto.setSuccess(false);
        return this.realTimeUserActionEsServiceImpl.getActionByUserDeviceId(parameterDto);
    }
}
