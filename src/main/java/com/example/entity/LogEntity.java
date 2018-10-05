package com.example.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author lqs2
 * @description log实体
 * @date 2018/10/4, Thu
 */
@Data
public class LogEntity {
    String id;
    String timeStamp;
    String remoteIp;
    String requestUrl;
    String requestMethod;
    Map<String, String[]> requestArgs;
    String response;

}
