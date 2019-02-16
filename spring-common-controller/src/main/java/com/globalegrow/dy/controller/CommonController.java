package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public abstract class CommonController {

    @ExceptionHandler({Exception.class})
    public ExceptionDto databaseError(HttpServletRequest req, Exception e) {
        log.error("调用异常，入口：" + req.getRequestURI(), e);
        ExceptionDto responseDto = new ExceptionDto();
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

}
