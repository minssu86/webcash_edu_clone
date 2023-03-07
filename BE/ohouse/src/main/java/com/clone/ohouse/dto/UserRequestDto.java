package com.clone.ohouse.dto;

import lombok.Getter;
import lombok.Setter;

public class UserRequestDto {

    @Getter
    @Setter
    public static class UserEmail{
        private String email;
    }

    @Getter
    @Setter
    public static class CheckCode{
        private String email;
        private String code;
    }

    @Getter
    public static class signup{
        private String email;
        private String password;
        private String nickname;
        private String token;
    }

}
