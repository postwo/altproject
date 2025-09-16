package com.example.altproject.handler;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException, IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String email;
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
        } else if ("google".equals(registrationId)) {
            email = oAuth2User.getAttribute("email");
        } else {
            throw new RuntimeException("지원하지 않는 OAuth Provider");
        }

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

        response.setContentType("application/json;charset=UTF-8");
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        new ObjectMapper().writeValue(response.getWriter(), tokenMap);
    }
}
