package com.example.altproject.common;

import com.example.altproject.filter.CustomUserDetails;
import com.example.altproject.service.oauth.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    /**
     * 현재 요청을 보낸 사용자가 유효한 로그인 상태인지 검증합니다.
     * 익명 사용자이거나 인증되지 않았다면 RuntimeException을 발생시킵니다.
     */
    public void validateLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 1. authentication 객체가 null이거나
        // 2. 인증되지 않았거나
        // 3. 익명 사용자(anonymousUser)일 경우, 예외 발생
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("로그인한 사용자만 이용 가능합니다.");
        }
    }
}