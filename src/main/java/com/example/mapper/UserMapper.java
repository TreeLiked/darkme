package com.example.mapper;

import com.example.entity.Msg;
import com.example.entity.User;
import com.example.entity.User_Friend;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/16, Mon
 */
@Mapper
@Repository
public interface UserMapper {

    @Insert("insert into user  values (null, #{username} , #{password} , #{email}, #{isMan}, null, null)")
    Integer insertUser(@Param("username") String username, @Param("password") String password, @Param("email") String email, @Param("isMan") boolean isMan) throws Exception;

    @Insert("insert into user_fri  values (null, #{user} , #{fri}, null,  0, null)")
    Integer wantMakeFriend(@Param("user") String user_id, @Param("fri") String fri_id) throws Exception;

    @Insert("insert into user_msg  values (null, #{user} , #{fri}, #{type}, #{msg}, #{btn_text}, 0, null)")
    Integer sendMsg(@Param("user") String user_id, @Param("fri") String fri_id, @Param("type") int type, @Param("msg") String msg, @Param("btn_text") String btn_text) throws Exception;

    @Insert("insert into user_handoff_text values (#{userId}, null, #{text}, null, 1)")
    Integer firstPostUserHandoffText(@Param("userId") String un, @Param("text") String text);


    @Delete("delete from user_msg where id = #{id}")
    Integer rmMsg(@Param("id") int id) throws Exception;

    @Delete("delete from user_fri where ( binary user_id = #{user_id} and binary fri_id = #{fri_id} or binary user_id = #{fri_id} and binary fri_id= #{user_id} ) and success = 1")
    Integer rmFri(@Param("user_id") String user, @Param("fri_id") String fri) throws Exception;


    @Update("update user_fri set success = 1 where binary user_id = #{user} and binary fri_id = #{fri} or binary user_id = #{fri} and binary fri_id = #{user}")
    Integer agreeMakeFriend(@Param("user") String u1, @Param("fri") String u2) throws Exception;

    @Update("update user_fri set fri_remark = #{re} where binary user_id = #{user} and binary fri_id = #{fri} and success = 1")
    Integer modRemark(@Param("user") String username, @Param("fri") String u1, @Param("re") String r) throws Exception;

    @Update("update user_handoff_text set wtp_text = #{text} , wtp_flag= 1 where binary userId = #{userId}")
    Integer postUserHandoffText(@Param("userId") String un, @Param("text") String text);

    @Update("update user_handoff_text set ptw_flag= 0 where binary userId = #{userId}")
    void disablePhoneToWeb(@Param("userId") String un) throws Exception;


    @Select("select * from user where binary username = #{username} and binary password = #{password}")
    User hasMatchUser1(@Param("username") String username, @Param("password") String password) throws Exception;

    @Select("select * from user where binary email = #{email} and binary password = #{password}")
    User hasMatchUser2(@Param("email") String email, @Param("password") String password) throws Exception;

    @Select("select * from user where binary username = #{username}")
    User addUser1(@Param("username") String username) throws Exception;

    @Select("select * from user where binary email = #{email}")
    User addUser2(@Param("email") String email) throws Exception;

    @Select("select count(*) from user where binary username = #{username}")
    Integer hasMatcherUsername(@Param("username") String name) throws Exception;

    @Select("select * from user_fri where binary user_id = #{username} and success = 1")
    List<User_Friend> findMyFriends(@Param("username") String username) throws Exception;

    @Select("select * from user_msg where binary msg_to = #{username} order by msg_time desc")
    List<Msg> getUserMsg(@Param("username") String username) throws Exception;

    @Select("select count(*) from user_msg where binary msg_to = #{username} and msg_state = 0")
    Integer getMsgAmountByUsername(@Param("username") String username) throws Exception;

    @Select("select count(*) from user_fri where ( binary user_id = #{user_id} and binary fri_id = #{fri_id} or binary user_id = #{fri_id} and binary fri_id= #{user_id} ) and success =  1")
    Integer judgeHaveBeenFriend(@Param("user_id") String user, @Param("fri_id") String fri) throws Exception;

    @Select("select count(*) from user_fri where binary user_id = #{user_id} and binary fri_id = #{fri_id} and success = 0")
    Integer haveSendFriendReq(@Param("user_id") String user, @Param("fri_id") String fri) throws Exception;

//    @Select("select * from user_handoff_text where binary userId = #{userId}")
//    Integer initUserHandoff(@Param("userId") String un) throws Exception;

    @Select("select ptw_text from user_handoff_text where binary userId = #{userId} and ptw_flag = 1")
    String getUserHandoffText(@Param("userId") String un) throws Exception;

}
