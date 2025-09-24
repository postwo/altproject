package com.example.altproject.common;

import com.example.altproject.filter.CustomUserDetails;
import com.example.altproject.service.oauth.CustomOAuth2User;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public String getEmail(Object principal) {
        if (principal instanceof CustomOAuth2User oauthUser) {
            return oauthUser.getEmail(); // OAuth2 사용자는 이메일이 식별자
        } else if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername(); // 일반 사용자는 사용자명이 식별자
        } else {
            throw new RuntimeException("인증된 사용자가 아닙니다.");
        }
    }

}