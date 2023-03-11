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

import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final HttpServletResponse response;
	
    @Value("${secretKey}")
    private String ENCRYPT_KEY;
    
    // prefix
    private final String ACCESS_HEADER = "Authorization";
    private final String REFRESH_HEADER = "RefreshToken";
    
    // valid time
    private final long ACCESS_VALID_TIME= 30*60*1000L; // 30분
    private final long REFRESH_VALID_TIME = 7*24*60*60*1000L; // 1주일

    
    // access token 생성
    public String createAccessToken(int userSeq) {
    	Claims claims = Jwts.claims().setSubject(userSeq+"");
    	String accessToken =  tokenProvider(claims, ACCESS_VALID_TIME);
    	response.addHeader(ACCESS_HEADER, "Bearer "+accessToken);
    	return accessToken;
    }
    
    // refresh token 생성
    public String createRefreshToken(int userSeq) {
    	Claims claims = Jwts.claims().setSubject("refresh!");
    	String refreshToken =  tokenProvider(claims, REFRESH_VALID_TIME);
    	response.addHeader(REFRESH_HEADER, "Bearer "+refreshToken);
    	InMemoryDBTemp.refreshTokenStorage.put(userSeq, refreshToken);
    	return refreshToken;
    }
    
    // sigup token 생성
    public String createSigupCheckToken(String email, String code) {
    	Map<String, String> claimMap = new HashMap<String, String>();
    	claimMap.put("email", email);
    	claimMap.put("code", code);
    	Claims claims = Jwts.claims();
    	claims.putAll(claimMap);
    	return tokenProvider(claims, 5*60*1000L);
    }
    
    /**
     * 토큰 생성
     * 1. 헤더의 타입(typ)을 지정할 수 있습니다. jwt를 사용하기 때문에 Header.JWT_TYPE로 사용해줍니다.
     * 2. 등록된 클레임 중, 토큰 발급자(iss)를 설정할 수 있습니다.
     * 3. 등록된 클레임 중, 발급 시간(iat)를 설정할 수 있습니다. Date 타입만 추가가 가능합니다.
     * 4. 등록된 클레임 중, 만료 시간(exp)을 설정할 수 있습니다. 마찬가지로 Date 타입만 추가가 가능합니다.
     * 5. 비공개 클레임을 설정할 수 있습니다. (key-value)
     * 6. 해싱 알고리즘과 시크릿 키를 설정할 수 있습니다.
     */
    public String tokenProvider(Claims claims, long validTime){
        Key key = Keys.hmacShaKeyFor(ENCRYPT_KEY.getBytes());
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT") // 1
                .setIssuedAt(now) // 3
                .setExpiration(new Date(now.getTime()+ validTime)) // 4
                .setClaims(claims)
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
