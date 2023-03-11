package com.clone.ohouse.service;

import com.clone.ohouse.domain.User;
import com.clone.ohouse.dto.UserRequestDto;
import com.clone.ohouse.dto.UserRequestDto.signin;
import com.clone.ohouse.mapper.UserMapper;
import com.clone.ohouse.util.EmailSender;
import com.clone.ohouse.util.InMemoryDBTemp;
import com.clone.ohouse.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    	if(userMapper.findOneByEmail(emailDto.getEmail())!=null) return ResponseEntity.ok("실패 : 아이디 중복");
        if(InMemoryDBTemp.checkCodeStorage.get(emailDto.getEmail())!=null) return ResponseEntity.ok("인증 코드가 이미 발송 되었습니다");
        String checkCode = createCheckCode();
        // todo : 메일 발송 코드 임시 정지
//        emailSender.sendCheckCodeToEmail(emailDto.getEmail(), checkCode);
        InMemoryDBTemp.checkCodeStorage.put(emailDto.getEmail(), checkCode);
        return ResponseEntity.ok("성공 : " + checkCode);
    }

    // 인증 코드 확인
    public ResponseEntity<?> doConfirmCheckCode(UserRequestDto.CheckCode checkCodeDto) {
        // 인증 : 이메일 + 코드
        if(!InMemoryDBTemp.checkCodeStorage.get(checkCodeDto.getEmail()).equals(checkCodeDto.getCode())) return ResponseEntity.ok("실패");
        String jwtToken = jwtUtil.createSigupCheckToken(checkCodeDto.getEmail(), checkCodeDto.getCode());
        return ResponseEntity.ok(jwtToken);
    }

    // 유저 정보 등록
    @Transactional
    public ResponseEntity<?> addNewUserInfo(UserRequestDto.signup signupDto) throws NoSuchAlgorithmException {
        // 토큰 인증
        try{
            boolean isResult = jwtUtil.confirmUserInfo(signupDto.getEmail(), signupDto.getToken());
            if(isResult){
                // db 저장
            	signupDto.setPassword(passwordEncoder(signupDto.getPassword()));
                userMapper.saveNewUserInfo(new User(signupDto));
                return ResponseEntity.ok("성공");
            }
        } catch (ExpiredJwtException e){
            return ResponseEntity.ok("시간 초과");
        }
        return ResponseEntity.ok("실패");
    }


    // 로그인
	public ResponseEntity<?> signin(signin signinDto) throws NoSuchAlgorithmException {
		User user = userMapper.findOneByEmail(signinDto.getEmail());
		if(user==null)return ResponseEntity.ok("없는 아이디 입니다.");
		if(!user.getUserPw().equals(passwordEncoder(signinDto.getPassword())))
			return ResponseEntity.ok("비밀 번호가 잘못 되었습니다.");
		// 인가 토큰 생성
		InMemoryDBTemp.refreshTokenStorage.remove(user.getUserId());
		String accessToken = jwtUtil.createAccessToken(user.getUserId());
		String refreshToken = jwtUtil.createRefreshToken(user.getUserId());
		// 리플레시 토큰 생성
		
		
		return ResponseEntity.ok("로그인 성공,  token : " + accessToken + " RE : " + refreshToken);
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
    
    // 비밀번호 암호화
    private String passwordEncoder(String password) throws NoSuchAlgorithmException {
    	/**
    	 * MessageDigest : 해시 함수를위한 클래스
    	 * getInstance(String algorithm) :
    	 * 		입력한 해시 알고리즘을 수행하는  MessageDigest 객체 생성 
    	 * 		(NoSuchAlgorithmException 발생 가능)
    	 * update(byte[] input) : 생성된 객체 내에 저장된 digest 값 갱신
    	 * digest : update()를 실행, 해시 계산 완료 후 해시화된 값(byte[])을 반환한다.
    	 */
    	MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");
    	md.update(password.getBytes());
    	byte[] data = md.digest();
    	/**
    	 * byte[]배열을 DB저장을 위한 String 타입으로 변환
    	 */
    	StringBuilder hexPassword=new StringBuilder();
    	for(byte b : data) {
    		String hexString = String.format("%02x", b);
    		hexPassword.append(hexString);
    	}
		return hexPassword.toString();
    }

}
