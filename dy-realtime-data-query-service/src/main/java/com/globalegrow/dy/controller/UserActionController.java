package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.service.RealTimeUserActionService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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
    @Qualifier("realTimeUserActionEsServiceImpl")
    private RealTimeUserActionService realTimeUserActionEsServiceImpl;

    /**
     * 上线后删除
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "userActionResponseDto",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public UserActionResponseDto userAction(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException, ParseException {
        return this.realTimeUserActionEsServiceImpl.userActionData(parameterDto);
    }

    //

    /**
     * 与源文档保持一致
     * @param parameterDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "getUserInfo",produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    /*@HystrixCommand(fallbackMethod = "fallbackMethod",commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy",value = "SEMAPHORE"),
            @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests",value = "5000"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "4000"),
            @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests",value = "5000")})*/
    public UserActionResponseDto userActionInfo(@Validated @RequestBody UserActionParameterDto parameterDto) throws IOException, ParseException {
        return this.realTimeUserActionEsServiceImpl.userActionData(parameterDto);
    }

    public UserActionResponseDto fallbackMethod(UserActionParameterDto userActionParameterDto) {
        UserActionResponseDto actionResponseDto = new UserActionResponseDto();
        actionResponseDto.setMessage("服务降级");
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
}
