package com.clone.ohouse.service;

import com.clone.ohouse.domain.User;
import com.clone.ohouse.dto.UserRequestDto;
import com.clone.ohouse.mapper.UserMapper;
import com.clone.ohouse.util.EmailSender;
import com.clone.ohouse.util.InMemoryDBTemp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EmailSender emailSender;
    private final UserMapper userMapper;

    public ResponseEntity<?> sendEmailWithCheckCode(UserRequestDto.UserEmail emailDto) {
        String checkCode = createCheckCode();
        // todo : 메일 발송 코드 임시 정지
//        emailSender.sendCheckCodeToEmail(emailDto.getEmail(), checkCode);
        InMemoryDBTemp.checkCode.put(emailDto.getEmail(), checkCode);
        return ResponseEntity.ok("성공 : " + checkCode);
    }

    public ResponseEntity<?> doConfirmCheckCode(UserRequestDto.CheckCode checkCode) {

        if(!InMemoryDBTemp.checkCode.get(checkCode.getEmail()).equals(checkCode.getCode())) return ResponseEntity.ok("실패");

        // todo : token 생성하여 응답 하는 코드 필요
        return ResponseEntity.ok("성공");
    }

    @Transactional
    public ResponseEntity<?> addNewUserInfo(UserRequestDto.signup signup) {
        // todo : 인증 코드 작성 필요
        // db 저장
        int result = userMapper.saveNewUserInfo(new User(signup));
        return ResponseEntity.ok("성공");
    }

    // 인증 코드 생성 메서드
    private String createCheckCode() {
        Random random = new Random();
        StringBuilder checkCode=new StringBuilder();
        for(int i=0; i<8; i++) {
            checkCode.append(random.nextInt(10));
        }
        return checkCode.toString();
    }

}