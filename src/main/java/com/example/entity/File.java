package com.example.entity;

import lombok.Data;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/6/21, Thu
 */
@Data
public class File {

    private int id;
    private String file_name;
    private String file_size;
    private String file_bring_id;

    private String file_bucket_id;

    private int file_save_days;
    private String file_attach;

    private String file_post_author;
    private String file_destination;
    private String file_post_date;

}
