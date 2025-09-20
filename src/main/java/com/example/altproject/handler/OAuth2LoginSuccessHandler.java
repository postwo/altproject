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

        // 프론트로 accessToken 전달
        // 이거는 프론트 포트 번호와 토큰값만 추가 해서 보내면 된다
        response.sendRedirect("http://localhost:5173/oauth2/callback/kakao?accessToken="+ accessToken);
    }
}
