package com.clone.ohouse.service;

import com.clone.ohouse.domain.User;
import com.clone.ohouse.dto.UserRequestDto;
import com.clone.ohouse.mapper.UserMapper;
import com.clone.ohouse.util.EmailSender;
import com.clone.ohouse.util.InMemoryDBTemp;
import com.clone.ohouse.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EmailSender emailSender;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    // 인증 코드 메일 전송
    public ResponseEntity<?> sendEmailWithCheckCode(UserRequestDto.UserEmail emailDto) {
        // email 중복 확인
        if(userMapper.findByEmail(emailDto.getEmail()).size()>0) return ResponseEntity.ok("email 중복");
        if(InMemoryDBTemp.checkCode.get(emailDto.getEmail())!=null) return ResponseEntity.ok("인증 코드가 이미 발송 되었습니다");
        String checkCode = createCheckCode();
        // todo : 메일 발송 코드 임시 정지
//        emailSender.sendCheckCodeToEmail(emailDto.getEmail(), checkCode);
        InMemoryDBTemp.checkCode.put(emailDto.getEmail(), checkCode);
        return ResponseEntity.ok("성공 : " + checkCode);
    }

    // 인증 코드 확인
    public ResponseEntity<?> doConfirmCheckCode(UserRequestDto.CheckCode checkCode) {
        // 인증 : 이메일 + 코드
        if(!InMemoryDBTemp.checkCode.get(checkCode.getEmail()).equals(checkCode.getCode())) return ResponseEntity.ok("실패");
        String jwtToken = jwtUtil.makeJwtToken(checkCode.getEmail(), checkCode.getCode());
        return ResponseEntity.ok(jwtToken);
    }

    // 유저 정보 등록
    @Transactional
    public ResponseEntity<?> addNewUserInfo(UserRequestDto.signup signup) {
        // 토큰 인증
        try{
            boolean isResult = jwtUtil.confirmUserInfo(signup.getEmail(), signup.getToken());
            if(isResult){
                // db 저장
                userMapper.saveNewUserInfo(new User(signup));
                return ResponseEntity.ok("성공");
            }
        } catch (ExpiredJwtException e){
            return ResponseEntity.ok("시간 초과");
        }
        return ResponseEntity.ok("실패");
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