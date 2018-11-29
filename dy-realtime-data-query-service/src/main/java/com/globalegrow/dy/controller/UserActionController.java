package com.globalegrow.dy.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.service.RealTimeUserActionHbaseService;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("user")
public class UserActionController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("realTimeUserActionEsServiceImpl")
    private RealTimeUserActionService realTimeUserActionEsServiceImpl;

    @Autowired
    @Qualifier("realTimeUserActionHbaseServiceImpl")
    private RealTimeUserActionHbaseService realTimeUserActionHbaseServiceImpl;

    @PostConstruct
    public void before() {
        HystrixThreadPoolProperties.Setter().withCoreSize(40);
    }

    @RequestMapping(value = "getUserInfoFromHbase",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto getUserInfoFromHbase(@Validated @RequestBody UserActionParameterDto parameterDto){
        return this.realTimeUserActionHbaseServiceImpl.getUserActionDataFromHbase(parameterDto);
    }

    /**
     * 与源文档保持一致
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @SentinelResource(value = "getUserInfo", blockHandler = "exceptionHandler")
    @RequestMapping(value = "getUserInfo",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    /*@HystrixCommand(fallbackMethod = "fallbackMethod",commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy",value = "SEMAPHORE"),
            @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests",value = "5000"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "4000")})*/
    @HystrixCommand(fallbackMethod = "fallbackMethod",commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy",value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")})
    public UserActionResponseDto userActionInfo(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException, ParseException {
        //long start = System.currentTimeMillis();
        UserActionResponseDto responseDto =  this.realTimeUserActionEsServiceImpl.userActionData(parameterDto);
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
        //this.logger.info("总时长: {}", System.currentTimeMillis() - start);
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

    @RequestMapping(value = "mockTest")
    public UserActionResponseDto userActionInfoMock(@Validated @RequestBody UserActionParameterDto parameterDto) {
        return this.realTimeUserActionEsServiceImpl.mock(parameterDto);
    }

    @RequestMapping("mock")
    public String mock() {
        return "success";
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
        logger.warn("服务超时或繁忙"+parameterDto.toString());
        UserActionResponseDto actionResponseDto = new UserActionResponseDto();
        actionResponseDto.setMessage("服务超时或繁忙");
        actionResponseDto.setSuccess(false);
        return actionResponseDto;
    }
}
