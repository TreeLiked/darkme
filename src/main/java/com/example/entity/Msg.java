package com.example.entity;

import lombok.Data;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/27, Fri
 */
@Data
public class Msg {

    private int id;
    private String msg_from;
    private String msg_to;

//    0系统消息；1用户消息； 2文件消息
    private int msg_type;
    private String msg_content;
    private String msg_btn_text;
    private String msg_state;
    private String msg_time;

}
