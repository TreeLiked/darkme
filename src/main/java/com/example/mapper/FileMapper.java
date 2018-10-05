package com.example.mapper;

import com.example.entity.File;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/6/21, Thu
 */
@Mapper
@Repository
public interface FileMapper {


    @Select("select * from file_lists order by file_post_date desc")
    List<File> getFileLists() throws Exception;

    @Select("select * from file_lists where binary file_post_author = #{username} or binary file_destination= #{username} order by file_post_date desc")
    List<File> getFileByUser(@Param("username")String username) throws Exception;

    @Insert("insert into file_lists values (" +
                "null, #{fileName}, #{size}, #{bringNo}, #{bucketNo}, #{saveDays}, #{attachment}, #{author},                    #{destination}, null)")
    Integer insertFileRecord(
            @Param("bringNo") String bringNo,
            @Param("bucketNo") String bucketNo,
            @Param("saveDays") int saveDays,
            @Param("fileName") String fileName,
            @Param("attachment") String attachment,
            @Param("destination") String destination,
            @Param("size") String fileSize,
            @Param("author") String file_post_author
    ) throws Exception;


    @Select("select * from file_lists where file_bring_id = #{bringNo}")
    File bringFileOut(@Param("bringNo") String file_bring_id) throws Exception;

    @SuppressWarnings("SpellCheckingInspection")
    @Select("select grfn();")
    String getFileRandomNo() throws Exception;


    @Delete("delete from file_lists where id = #{id}")
    Integer removeFileById(@Param("id") String bring_id) throws Exception;

    @Select("select count(*) from file_lists where binary file_post_author = #{username} or binary file_destination= #{username} ")
    Integer getFileAmountByUsername(@Param("username") String username);

//    用存储过程
//    @Delete("delete * from numbers where r1 = #{}")
//    Integer deleteNo(int r1) throws Exception;

}
