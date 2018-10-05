package com.example.entity;

import lombok.Data;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/27, Fri
 */
@Data
public class User_Friend {

    private int id;
    private String user_id;
    private String fri_id;
    private String fri_remark;
    private int success;
    private String create_time;

}
