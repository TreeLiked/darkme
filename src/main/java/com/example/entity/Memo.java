package com.example.entity;

import lombok.Data;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/25, Wed
 */
@Data
public class Memo {
    private int id;
    private String memo_author;
    private String memo_title;
    private String memo_content;
    private int memo_type;
    private int memo_state;
    private String memo_post_date;
}
