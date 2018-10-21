package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lqs2
 * @description 全局异常处理
 * @date 2018/8/3, Fri
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ControllerAdvice(basePackages = {"com.example.controller", "com.example.utils"})
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String errorHandle(HttpServletRequest request, Exception e) {
//        实际开发写入日志
//        String remoteIp = request.getRemoteAddr();
//        String requestUrl = request.getRequestURL().toString();
//        String requestMethod = request.getMethod();
//        request.getRequestURI();
        return "error";
    }
}
