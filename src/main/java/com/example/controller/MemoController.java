package com.example.controller;

import com.example.mapper.MemoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/25, Wed
 */
@RestController
@Slf4j
@EnableTransactionManagement
public class MemoController {


    private final MemoMapper memoMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public MemoController(MemoMapper memoMapper, ObjectMapper objectMapper) {
        this.memoMapper = memoMapper;
        this.objectMapper = objectMapper;
    }


    @RequestMapping(value = "getMemo", method = {RequestMethod.GET})
    public String getMemoByMemoState(Boolean memo_state, HttpSession session) {
        String username = (String) session.getAttribute("un");

        if (username != null) {
            List mMemoList;
            try {
                mMemoList = memoMapper.findMemosByState(username, memo_state);
                return objectMapper.writeValueAsString(mMemoList);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "fail";
    }

    @RequestMapping("modMyMemo")
    @Transactional
    public String modifyMyMemo(int id, int toState, boolean isRemoved) {
        Integer i;
        try {
            if (isRemoved) {
                i = memoMapper.removeMemoById(id);
            } else {

                i = memoMapper.modifyMemoStateById(id, toState);
            }
            return i == 1 ? "1" : "0";
        } catch (Exception e) {
            log.error(e.toString());
        }
        return "0";
    }



    @RequestMapping(value = "newMemo", method = {RequestMethod.POST})
    @Transactional
    public String addNewMemo(String u1, String u2, boolean u3, HttpServletRequest request, HttpSession session ){

        String username = (String) session.getAttribute("un");
        if (username != null) {
            try {
                Integer i = memoMapper.addNewMemo(username, u1, u2, u3);
                return i == 1? "1": "0";
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "0";
    }

    @RequestMapping(value = "searchMemo", method = RequestMethod.GET)
    @Transactional
    public String searchMemo(String u1, HttpSession session) {

        String username = (String) session.getAttribute("un");
        if (username != null) {
            List list;
            try {
                list = memoMapper.searchMemo(username, u1);
                return objectMapper.writeValueAsString(list);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "0";
    }
//    @RequestMapping(value = "getMemo1", method = {RequestMethod.GET})
//    public void getMemoFinished(HttpServletResponse response, HttpSession session, Model model) {
//        String username = (String) session.getAttribute("un");
//
//        if (username != null && !("").equals(username)) {
//
//        }
//
//    }
}
