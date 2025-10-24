package com.example.altproject.util;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${spring.jwt.secret-key}") String secretKey) {
        // 💡 수정된 부분: Base64로 인코딩된 secretKey를 Decoders.BASE64로 디코딩합니다.
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateAccessToken (String email, List<String> roles){
        long expiration_30m = 1000L * 60 * 60;

        return Jwts.builder()
                .setSubject(email)
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration_30m))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken (String email){
        long expiration_7d = 1000L * 60 * 60 * 24 * 7;

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration_7d))
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(ErrorStatus.INVALID_TOKEN, e , "유효하지 않은 토큰입니다.");
        }
    }

}
