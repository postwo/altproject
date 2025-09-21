package com.example.altproject.handler;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.service.oauth.CustomOAuth2User;
import com.example.altproject.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // CustomOAuth2User로 캐스팅
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // OAuth2AuthenticationToken을 사용하여 프로바이더(제공자) 이름 가져오기
        String provider;
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            // 등록된 클라이언트의 ID, 즉 'google' 또는 'kakao'를 가져옵니다.
            provider = oauthToken.getAuthorizedClientRegistrationId();
        } else {
            // OAuth2 인증이 아닐 경우 예외 처리
            throw new IllegalArgumentException("OAuth2AuthenticationToken이 아닙니다.");
        }

        // oAuth2User.getAttributes() 안에 email이 이미 있음
        String email = customOAuth2User.getEmail();
        if (email == null) throw new RuntimeException("OAuth2 로그인 이메일 없음");

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER,"사용자 없음"));

        List<String> roles = member.getMemberRoleList().stream()
                .map(Enum::name)
                .toList();
        String accessToken = jwtTokenProvider.generateAccessToken(email, roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        redisTemplate.opsForValue().set(email, refreshToken, Duration.ofDays(7));

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshCookie);

        // 프로바이더에 따라 동적으로 리디렉션 URL 생성
        String redirectUrl = "http://localhost:5173/oauth2/callback/" + provider + "?accessToken=" + accessToken;
        response.sendRedirect(redirectUrl);
    }
}
