package com.example.controller;

import com.example.mapper.FileMapper;
import com.example.entity.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/6/21, Thu
 */
@Transactional
@RestController
public class TestController {


    //    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private final FileMapper fileMapper;

    @Autowired
    public TestController(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    //    @Test
    @RequestMapping("/get")
    public String get() throws Exception {
        List<File> files = fileMapper.getFileLists();
        files.forEach(a -> System.out.println(a.toString()));
        return files.toString();
    }

//    @RequestMapping("/hello")
//    @ResponseBody
//    public String hello() {
//        return "hello";
//    }
}
