package com.example.config;

import com.example.entity.LogEntity;
import com.example.utils.OsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

/**
 * @author lqs2
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Date date = new Date();


    private static String separator = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

    @Pointcut("execution(public * com.example.controller.*.*(..))")
    public void webLog() {
    }

//    /**
//     * 拦截请求参数信息
//     */
//    @Before("webLog()")
//    public void doBefore(JoinPoint joinPoint) throws Throwable {
//
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        assert attributes != null;
//        HttpServletRequest request = attributes.getRequest();
//        String remoteIp = request.getRemoteAddr();
//        String requestUrl = request.getRequestURL().toString();
//        String requestMethod = request.getMethod();
//
//        logger.info("Remote IP: " + remoteIp);
//        logger.info("Request URL: " + requestUrl);
//        logger.info("Request METHOD: " + requestMethod);
//        Enumeration<String> enu = request.getParameterNames();
//        int i = 1;
//        while (enu.hasMoreElements()) {
//            String name = enu.nextElement();
//            String value = request.getParameter(name);
//            if (value.length() <= 100) {
//                logger.info("Param[{}]: name= {}, value= {}", i++, name, value);
//            } else {
//                logger.info("Param[{}]: name= {}, value= {}", i++, name, value.substring(0, 97));
//            }
//        }
//    }

    @AfterReturning(returning = "resp", pointcut = "webLog()")
    public void doAfterReturning(Object resp) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        LogEntity entity = new LogEntity();
        HttpServletRequest request = attributes.getRequest();
        String remoteIp = request.getRemoteAddr();
        String requestUrl = request.getRequestURL().toString();
        String requestMethod = request.getMethod();

        logger.info("Remote IP: {}", remoteIp);
        logger.info("Request URL: {}", requestUrl);
        logger.info("Request METHOD: {}", requestMethod);

//        Map<String, String[]> parameterMap =
//                request.getParameterMap();
//        Enumeration<String> enu = request.getParameterNames();
//        int i = 1;
//        while (enu.hasMoreElements()) {
//            String name = enu.nextElement();
//            String value = request.getParameter(name);
//            if (value.length() <= 100) {
//                logger.info("Param[{}]: name= {}, value= {}", i++, name, value);
//            } else {
//                logger.info("Param[{}]: name= {}, value= {}", i++, name, value.substring(0, 97));
//            }
//        }
        String response = "";
        if (resp instanceof String) {
            String value = (String) resp;
            if (value.length() > 1000) {
                response = value.substring(0, 97) + "...";
                logger.info("Response: {}", response);
            } else {
                response = (String) resp;
                logger.info("Response: {}", response);
            }
        }
        String finalResponse = response;
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
                    entity.setId(UUID.randomUUID().toString());
                    entity.setTimeStamp(LocalDate.now() + " " + LocalTime.now());
                    entity.setRemoteIp(remoteIp);
                    entity.setRequestUrl(requestUrl);
                    entity.setRequestMethod(requestMethod);
                    entity.setRequestArgs(request.getParameterMap());
                    entity.setResponse(finalResponse);
                    writeLog(entity);
                }
        );
    }

    private void writeLog(final LogEntity logEntity) {
        String path = "/Users/lqs2/Desktop/darkme-log.txt";
        if (OsUtils.isLinux()) {
            path = "/etc/darkme/darkme-log.txt";
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path), true));
            writer.write(separator);
            objectMapper.writeValue(writer, logEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
