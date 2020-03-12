package com.k2.resourceserver.controller;

import com.yunque.commons.util.RespBuilder;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author:West
 * @Date: create in 2018/5/17 全局异常
 */
@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<Object, Object> defaultExceptionHandler(HttpServletRequest req, Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return RespBuilder.errorJsonStr(e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Map<Object, Object> missingServletRequestParameterExceptionHandler(HttpServletRequest req, Exception e) {
        e.printStackTrace();
        return RespBuilder.errorJsonStr("params type error");
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Map<Object, Object> bindExceptionHandler(HttpServletRequest req, BindException e) {
        e.printStackTrace();
        if (!e.getBindingResult().getAllErrors().isEmpty()) {
            return RespBuilder.errorJsonStr(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        }
        return RespBuilder.errorJsonStr(e.getMessage());
    }
}
