package com.example.controller;

import com.example.mapper.FileMapper;
import com.example.mapper.MemoMapper;
import com.example.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/19, Thu
 */
@Slf4j
@Controller
public class PageController {


    private final FileMapper fileMapper;
    private final MemoMapper memoMapper;
    private final UserMapper userMapper;

    @Autowired
    public PageController(FileMapper fileMapper, MemoMapper memoMapper, UserMapper userMapper) {

        this.fileMapper = fileMapper;
        this.memoMapper = memoMapper;
        this.userMapper = userMapper;
    }

    @RequestMapping("/")
    public String goMain(HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        return "main";
    }

    @RequestMapping("hello")
    public String sayHello(HttpServletResponse response, HttpSession session, Model model) {
        return "hello";
    }


    @RequestMapping("index")
    public String main(HttpServletResponse response, Model model) {
        response.setContentType("text/html;charset=utf-8");
        model.addAttribute("subFrame", "hello");
        return "index";
    }


    @RequestMapping("myFile")
    public String mFile(HttpServletResponse response, HttpSession session, Model model) {
        response.setContentType("text/html;charset=utf-8");
        String username = (String) session.getAttribute("un");
        if (username != null) {
            List mFileList;
            try {
                mFileList = fileMapper.getFileByUser(username);
            } catch (Exception e) {
                log.error(e.toString());
                return "404";
            }
            model.addAttribute("mFilelist", mFileList);
            model.addAttribute("file_amount", mFileList.size());
        }
        return "file";
    }

    @RequestMapping("myMemo")
    public String mMemo() {
        return "memo";
    }

    @RequestMapping("myMsg")
    public String myMessage() {
        return "msg";
    }

    @RequestMapping("myFriend")
    public String mFriend() {
        return "friend";
    }

    @RequestMapping("goSource")
    public void goGit(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://github.com/TreeLiked/darkme.git");
    }

    @RequestMapping("countAll")
    @ResponseBody
    @Transactional
    public String getFileCount(HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (username != null) {
            Integer result1;
            Integer result2;
            Integer result3;
            try {
                result1 = fileMapper.getFileAmountByUsername(username);
                result2 = memoMapper.getMemoAmountByUsername(username);
                result3 = userMapper.getMsgAmountByUsername(username);
                return result1 + " " + result2 + " " + result3;
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "-1";
    }
}