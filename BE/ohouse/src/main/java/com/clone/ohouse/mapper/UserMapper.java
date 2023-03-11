package com.clone.ohouse.mapper;

import com.clone.ohouse.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    int saveNewUserInfo(User user);

//    @Select("select * from user_table where user_email = #{email}")
//    List<User> findOneByEmail(String email);
    

    @Select("select * from user_table where user_email = #{email}")
    User findOneByEmail(String email);

}
