package com.clone.ohouse.mapper;

import com.clone.ohouse.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int saveNewUserInfo(User user);

}
