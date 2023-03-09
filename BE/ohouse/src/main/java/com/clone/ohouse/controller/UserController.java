package com.clone.ohouse.controller;

import com.clone.ohouse.dto.UserRequestDto;
import com.clone.ohouse.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    /**
     * <h1>인증 코드 요청</h1>
     * 이메일을 통해 회원가입을 위한 인증 코드 발급
     * email : String
     */
    @PostMapping(path = "email-check")
    public ResponseEntity<?> sendEmailWithCheckCode(@RequestBody UserRequestDto.UserEmail emailDto) {
        return userService.sendEmailWithCheckCode(emailDto);
    }

    // 인증 코드 확인
    @PostMapping(path = "code-check")
    public ResponseEntity<?> doConfirmCheckCode(@RequestBody UserRequestDto.CheckCode checkCode){
        return userService.doConfirmCheckCode(checkCode);
    }

    // 회원 가입
    @PostMapping(path = "signup")
    public ResponseEntity<?> addNewUserInfo(@RequestBody UserRequestDto.signup signup){
        return userService.addNewUserInfo(signup);
    }

}
