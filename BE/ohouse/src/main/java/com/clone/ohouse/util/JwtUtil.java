package com.clone.ohouse.util;

import com.clone.ohouse.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${secretKey}")
    private String ENCRYPT_KEY;

    /**
     * 토큰 생성
     * 1. 헤더의 타입(typ)을 지정할 수 있습니다. jwt를 사용하기 때문에 Header.JWT_TYPE로 사용해줍니다.
     * 2. 등록된 클레임 중, 토큰 발급자(iss)를 설정할 수 있습니다.
     * 3. 등록된 클레임 중, 발급 시간(iat)를 설정할 수 있습니다. Date 타입만 추가가 가능합니다.
     * 4. 등록된 클레임 중, 만료 시간(exp)을 설정할 수 있습니다. 마찬가지로 Date 타입만 추가가 가능합니다.
     * 5. 비공개 클레임을 설정할 수 있습니다. (key-value)
     * 6. 해싱 알고리즘과 시크릿 키를 설정할 수 있습니다.
     */
    public String makeJwtToken(String email, String code){
        Key key = Keys.hmacShaKeyFor(ENCRYPT_KEY.getBytes());
        Date now = new Date();
        return Jwts.builder()
                .setSubject("singup_code")
                .setHeaderParam("typ", "JWT") // 1
                .setIssuer("singup") // 2
                .setIssuedAt(now) // 3
                .setExpiration(new Date(now.getTime()+ Duration.ofMinutes(5).toMillis())) // 4
                .claim("email",email) // 5
                .claim("code",code) // 5
                .signWith(key, SignatureAlgorithm.HS256) // 6
                .compact();
    }

    // payload email 정보 일치 확인
    public boolean confirmUserInfo(String email, String token) throws ExpiredJwtException {
        Claims claims = getAllClaims(token);
        return email.equals(claims.get("email"));
    }

    // Claims(payload) 추출
    private Claims getAllClaims(String token) throws ExpiredJwtException {
        Key key = Keys.hmacShaKeyFor(ENCRYPT_KEY.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
