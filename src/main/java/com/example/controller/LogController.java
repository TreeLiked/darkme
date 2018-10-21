package com.example.controller;

import com.example.entity.LogEntity;
import com.example.utils.OsUtils;
import com.example.utils.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * @author lqs2
 * @description 日志controller
 * @date 2018/10/6, Sat
 */
@RestController
@RequestMapping("/log/api")
public class LogController {

    private final ObjectMapper objectMapper;

    @Autowired
    public LogController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RequestMapping(value = "/display", method = RequestMethod.GET)
    public String getLog(String un) {

        if (StringUtil.isEmpty(un) || !"admin".equals(un)) {
            return "Access denied.";
        }
        String path = "/Users/lqs2/Desktop/darkme-log.txt";
        if (OsUtils.isLinux()) {
            path = "/etc/darkme/darkme-log.txt";
        }
        File file = new File(path);
        if (!file.exists()) {
            return "No log file was found at path: " + path;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null && line.startsWith("{") && line.endsWith("}")) {
                builder.append(line).append(",");
            }
            String substring = builder.substring(0, builder.lastIndexOf(","));
            substring += "]";
            List<LogEntity> entityList = objectMapper.readValue(substring, new TypeReference<List<LogEntity>>() {
            });
            return objectMapper.writeValueAsString(entityList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error occurred when parsing log to json";
    }

    @RequestMapping(value = "download")
    public void downloadLog(String un, String pwd, String token, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtil.isEmpty(un) || StringUtil.isEmpty(pwd) || StringUtil.isEmpty(token)) {
            return;
        }
        if (!"admin".equals(un) && !"000000".equals(un)) {
            return;
        }
        String path = "/Users/lqs2/Desktop/darkme-log.txt";
        if (OsUtils.isLinux()) {
            path = "/etc/darkme/darkme-log.txt";
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        ServletContext context = request.getServletContext();
        String mimeType = context.getMimeType(path);
        if (null == mimeType) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setContentLength((int) file.length());
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
        response.setHeader(headerKey, headerValue);
        try {
            IOUtils.copy(new FileInputStream(file), response.getOutputStream());
        } catch (IOException ignored) {

        }
    }
}
