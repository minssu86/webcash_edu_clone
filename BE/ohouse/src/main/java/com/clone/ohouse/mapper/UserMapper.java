package com.clone.ohouse.mapper;

import com.clone.ohouse.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    int saveNewUserInfo(User user);

    @Select("select * from user where user_email = #{email}")
    List<User> findByEmail(String email);
}
