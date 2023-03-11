package com.clone.ohouse.domain;

import com.clone.ohouse.dto.UserRequestDto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {

    private int userId;
    private String userPw;
    private String userNickname;
    private String userEmail;
    private String userSocialKakao;
    private String userSocialNaver;


//    // 회원 가입용
    public User(UserRequestDto.signup signup){
        this.userNickname = signup.getNickname();
        this.userEmail = signup.getEmail();
        this.userPw = signup.getPassword();
    }

}
