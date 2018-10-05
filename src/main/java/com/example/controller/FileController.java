package com.example.controller;

import com.example.entity.File;
import com.example.mapper.FileMapper;
import com.example.mapper.UserMapper;
import com.example.utils.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @Author lqs2
 * @Description 文件控制类
 * @Date 2018/6/24, Sun
 */
@SuppressWarnings("Duplicates")
@RestController
@Slf4j
@Transactional(rollbackFor = RuntimeException.class)
public class FileController {


    private final FileMapper fileMapper;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Autowired
    public FileController(FileMapper fileMapper, ObjectMapper objectMapper, UserMapper userMapper) {
        this.fileMapper = fileMapper;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
    }

    @RequestMapping(value = "generateFileNo", method = {RequestMethod.GET})
    public String generateFileNo(String fileName) {
        String r1;
        try {
            r1 = fileMapper.getFileRandomNo();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if (!StringUtil.isEmpty(r1)) {
                return r1 + "/" + r1 + suffix;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return "0";
    }


    @SuppressWarnings("ConstantConditions")
    @RequestMapping(value = "generateNewFile", method = {RequestMethod.POST})
    public String generateNewFile(String fileNo, String fileName, String attachment, String destination, String fileSize, HttpSession session) {


        String filePostAuthor = (String) session.getAttribute("un");
        int fileSaveDays = 1;
        if (filePostAuthor != null) {
            fileSaveDays = 365;
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            if (destination != null) {
                userMapper.sendMsg(filePostAuthor, destination, 2, "收到一个文件【 名称：" + fileName + "，大小：" + fileSize + "，编号：" + fileNo + " 】，来源：" + filePostAuthor, "null");
            }
            fileMapper.insertFileRecord(fileNo, fileNo + "." + suffix, fileSaveDays, fileName, attachment, destination, fileSize, filePostAuthor);
            return fileNo + " " + (fileNo + "." + suffix) + " " + fileSaveDays;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return "0";
    }

    @RequestMapping("bringFile")
    public String getFile(String bringNo) throws Exception {

        File file = fileMapper.bringFileOut(bringNo);
        if (file.getFile_name() != null) {
            return file.getFile_bucket_id() + " " + file.getFile_name();
        } else {
            return "null";
        }
    }

    @RequestMapping(value = "getObjectDetail", method = {RequestMethod.GET})
    public String getObjectDetail(String bringNo, HttpSession session) throws Exception {
        File file = fileMapper.bringFileOut(bringNo);
        if (file != null) {
            if (file.getFile_destination() != null && !"".equals(file.getFile_destination())) {
                String username = (String) session.getAttribute("un");
                if (username != null) {
                    if ((username.equals(file.getFile_destination()) || file.getFile_post_author().equals(username))) {
                        return objectMapper.writeValueAsString(file);
                    }
                }
                return "no";
            }
            return objectMapper.writeValueAsString(file);
        }
        return "null";
    }

    @RequestMapping(value = "rmFile", method = {RequestMethod.GET})
    public String removeFile(String file_id, String bucket_id) {
        Integer result;
        try {
            result = fileMapper.removeFileById(file_id);
            new CosController().rmFile(bucket_id);
            return result == 1 ? "success" : "fail";
        } catch (Exception e) {
            log.error(e.toString());
        }
        return "fail";
    }

}
