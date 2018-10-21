package com.example.controller;

import com.example.algorithm.Alus;
import com.example.entity.StrokeData;
import com.example.entity.stroke.Loc;
import com.example.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 小游戏controller
 *
 * @author lqs2
 * @date 2018/10/20, Sat
 */
@Controller
@RequestMapping("/entertainment")
@Slf4j
public class RecreationController {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/onestroke", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String playOneStroke(String data, String token) {
        if (StringUtil.isEmpty(token) || StringUtil.isEmpty(data)) {
            return "-1";
        }
        try {
            StrokeData value = objectMapper.readValue(data, StrokeData.class);
            AtomicBoolean result = new AtomicBoolean(true);
//            ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
            Stack<Loc> locStack = new Stack<>();
            Alus.calPath(locStack, value.getStartX(), value.getStartY(), value.getData());
            if (locStack.size() == 0) {
                return "0";
            }
            List<Loc> locs = new ArrayList<>(locStack);
            try {
                return objectMapper.writeValueAsString(locs);
            } catch (JsonProcessingException e) {
                log.error(e.getCause().toString());
            }
//            });
        } catch (IOException e) {
            log.error(e.getCause().toString());
        }
        return "-1";
    }
}
