package com.example.controller;

import com.example.entity.Msg;
import com.example.entity.User;
import com.example.entity.User_Friend;
import com.example.mapper.UserMapper;
import com.example.utils.CookieUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/16, Mon
 */
@RestController
@Transactional(rollbackFor = {})
@Slf4j
public class UserController {


    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserMapper userMapper, ObjectMapper objectMapper) {
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }


    @RequestMapping(value = "lv", method = {RequestMethod.POST})
    public String loginValidate(String u1, String u2, boolean u3, HttpServletRequest request, HttpServletResponse response) throws Exception {


        User user;
        if (u1.contains("@")) {
//            以邮箱登录
            user = userMapper.hasMatchUser2(u1, u2);
        } else {
            user = userMapper.hasMatchUser1(u1, u2);
        }
        if (user == null) {
            return "0";
        }

        HttpSession session = request.getSession();
        session.setAttribute("un", user.getUsername());
        session.setAttribute("ue", user.getEmail());

        Cookie cookie = new Cookie("user_info_cookie", session.getId());
        if (u3) {
            session.setMaxInactiveInterval(3600 * 24 * 15);
            cookie.setMaxAge(3600 * 24 * 15);

        } else {
            session.setMaxInactiveInterval(1800);
            cookie.setMaxAge(1800);
        }

        request.getServletContext().setAttribute(session.getId(), session);
        response.addCookie(cookie);
        return "1" + user.getUsername();

    }

    @RequestMapping(value = "haveRememberMe", method = {RequestMethod.GET})
    public String haveRememberMe(String u1, HttpServletRequest request, HttpSession session) {
        if (u1 != null) {
            HttpSession user_session = (HttpSession) request.getServletContext().getAttribute(u1);
            if (user_session != null) {
                session.setAttribute("un", user_session.getAttribute("un"));
                return "1 " + user_session.getAttribute("un");
            }
        }
        return "0 ";
    }


    @RequestMapping(value = "rv", method = {RequestMethod.POST})
    public String registerValidate(String u1, String u2, String u3, boolean u4) {

        Integer update;
        try {
            update = userMapper.insertUser(u1, u2, u3, u4);
        } catch (Exception e) {
            update = 0;
            log.error("注册出现异常{}", e);
        }
        return update == 1 ? "1" : "0";
    }

    @SuppressWarnings("SpellCheckingInspection")
    @RequestMapping(value = "/trunie", method = {RequestMethod.GET})
    public String testRegisterUsernameIsExist(String u1) {

        Integer integer = 0;
        try {
            integer = userMapper.hasMatcherUsername(u1);
        } catch (Exception e) {
            log.error("注册判断是否账号被使用出现异常{}", e);
        }
        return integer == 0 ? "1" : "0";
    }

    @RequestMapping(value = "lff", method = {RequestMethod.GET})
    public String lookForFriends(HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (username != null) {
            try {
                List<User_Friend> list;
                Map<String, String[]> map = new HashMap<>();
                list = userMapper.findMyFriends(username);
                list.forEach(user_friend -> {
                    if (!user_friend.getFri_id().equals(username)) {
                        map.put(user_friend.getFri_id(), new String[]{user_friend.getFri_remark(), user_friend.getCreate_time()});
                    }
                    if (!user_friend.getUser_id().equals(username)) {
                        map.put(user_friend.getUser_id(), new String[]{user_friend.getFri_remark(), user_friend.getCreate_time()});
                    }
                });
                return objectMapper.writeValueAsString(map);
            } catch (Exception e) {
                log.error(e.toString());

            }
        }
        return "0";
    }

    @RequestMapping(value = "loginOut", method = {RequestMethod.GET})
    public void loginOut(String u1, HttpServletRequest request) {
        try {
            Cookie cookie = CookieUtils.getCookieByName(request, "user_info_cookie");
            if (cookie != null) {
                cookie.setMaxAge(0);
            }
            HttpSession session = (HttpSession) request.getServletContext().getAttribute(u1);
            if (session != null) {
                session.invalidate();
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    @RequestMapping(value = "makeOneFriend", method = {RequestMethod.GET})
    public String makeOneFriend(String u1) {
        User user;
        try {
            if (!u1.contains("@")) {
                user = userMapper.addUser1(u1);
            } else {
                user = userMapper.addUser2(u1);
            }
            if (user != null) {
                return user.getUsername() + " " + (user.is_man() ? "男" : "女");
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return "0";
    }

    @RequestMapping(value = "smkfrqu", method = {RequestMethod.GET})
    public String sendMakeFriendRequest(String sw, String url, HttpServletResponse response, HttpSession session) {

        response.setContentType("text/htm;charset=UTF-8");
        response.setHeader("Refresh", "1;url=" + url);
        String username = (String) session.getAttribute("un");
        if (username != null) {
            if (sw != null) {
                if (username.equals(sw)) {
                    return "不能添加自己";
                }
                try {
                    Integer i1 = userMapper.judgeHaveBeenFriend(username, sw);
                    if (i1 >= 1) {
                        return "已经是好友，请勿重复添加";
                    }
                    Integer i2 = userMapper.haveSendFriendReq(username, sw);
                    if (i2 >= 1) {
                        return "勿重复发送好友请求或对方已经向您发送了好友请求";
                    }
                    Integer i3 = userMapper.wantMakeFriend(username, sw);
                    userMapper.wantMakeFriend(sw, username);
                    Integer i5 = userMapper.sendMsg(username, sw, 1, "来自【用户：" + username + "】的好友添加请求", "同意");
                    return i3 == 1 && i5 == 1 ? "请求发送成功" : "请求添加失败";

                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        }
        return "请求添加失败，您还没有登录";
    }

    @RequestMapping(value = "gtmymsg", method = {RequestMethod.GET})
    public String getMyMessage(HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (username != null) {
            try {
                List<Msg> msgs = userMapper.getUserMsg(username);
                if (msgs != null) {
                    return objectMapper.writeValueAsString(msgs);
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "0";
    }
//    agrMkFriReq

    @RequestMapping(value = "agrMkFriReq", method = {RequestMethod.GET})
    public String getMyMessage(int u0, String u1, String u2, HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (username != null && u1 != null && u2 != null) {
            try {
                Integer i1 = userMapper.agreeMakeFriend(u1, u2);
//                Integer i2 = userMapper.agreeMakeFriend(u2, u1);
                userMapper.rmMsg(u0);
                if (i1 >= 1) {
                    return "1";
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "0";
    }


    @RequestMapping(value = "rmMsg", method = {RequestMethod.GET})
    public String removeMessage(Integer u0) {
        if (u0 != null) {
            try {
                return userMapper.rmMsg(u0) == 1 ? "1" : "0";
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "0";
    }

    @RequestMapping(value = "rmFri", method = {RequestMethod.GET})
    public String delFri(String fri, HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (username != null) {
            try {
                return userMapper.rmFri(username, fri) == 1 ? "1" : "0";
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return "0";
    }

    @RequestMapping(value = "smsg", method = {RequestMethod.GET})
    public String sendMessage(String u1, String u2, HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (username != null) {
            if (u1 != null && u2 != null) {
                try {
                    return userMapper.sendMsg(username, u2, 1, "来自【 " + username + " 】" + u1, "null") == 1 ? "1" : "0";
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        }
        return "0";
    }

    @RequestMapping(value = "modrem", method = {RequestMethod.GET})
    public String modifyRemark(String u1, String r, HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (username != null) {
            if (u1 != null) {
                try {
                    return userMapper.modRemark(username, u1, r) == 1 ? "1" : "0";
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        }
        return "0";
    }

    @RequestMapping(value = "guhtfp", method = {RequestMethod.GET})
    public String getUserHandoffTextFromPhone(String un, HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (null != username) {
            try {
                String text = userMapper.getUserHandoffText(un);
                if (!StringUtils.isEmpty(text)) {
                    return text;
                } else {
                    return "0";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "-1";
    }


    @RequestMapping(value = "puhttp", method = {RequestMethod.GET})
    public String postUserHandoffTextToPhone(String un, String text, HttpSession session) {
        String username = (String) session.getAttribute("un");
        if (!StringUtils.isEmpty(username)) {
            log.info("{} 推送文本到手机", username);
            try {
                Integer i = userMapper.postUserHandoffText(un, text);
                if (1 != i) {
                    Integer i1 = userMapper.firstPostUserHandoffText(un, text);
                    if (1 == i1) {
                        return "success";
                    } else {
                        return "-1";
                    }
                } else {
                    return "success";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("无名推送被拒绝");
        return "-1";
    }

    @RequestMapping(value = "offptw", method = {RequestMethod.GET})
    public void disablePhoneToWeb(String un) {
        try {
            log.info("关闭此次推送" + un);
            userMapper.disablePhoneToWeb(un);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}