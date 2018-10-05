package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/8/3, Fri
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ControllerAdvice(basePackages = {"com.example.controller", "com.example.utils"})
public class GlobalExceptionHandler {

    //    @ExceptionHandler(Exception.class)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> catchError() {
//        实际开发写入日志
        Map<String, Object> errorResultMap = new HashMap<>();
        errorResultMap.put("error_code", "500");
        errorResultMap.put("error_msg", "系统错误");
        return errorResultMap;
    }
}
