package com.example.altproject.service.implement;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.dto.request.SignInRequest;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.SignInResponse;
import com.example.altproject.dto.response.SignUpResponse;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.service.MemberService;
import com.example.altproject.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImplement implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;


    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(ErrorStatus.DUPLICATE_EMAIL);
        }

        if (memberRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new ApiException(ErrorStatus.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.createMember(request.getEmail(), encodedPassword, request.getNickname());
        Member savedMember = memberRepository.save(member);

        SignUpResponse response = SignUpResponse.responseMember(savedMember);
        return response;
    }

    private String createAccessToken(Member member) {
        List<String> roles = member.getMemberRoleList().stream()
                .map(Enum::name)
                .toList();

        return jwtTokenProvider.generateAccessToken(member.getEmail(), roles);
    }


    @Override
    @Transactional
    public SignInResponse signIn(SignInRequest request) {

        Member user = memberRepository.getWithRoles(request.getEmail()).orElseThrow(()->new ApiException(ErrorStatus.NOT_EXISTED_USER));


        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus.PASSWORD_MISMATCH);
        }

        String accessToken = createAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(user.getEmail(),refreshToken, Duration.ofDays(7));

        return new SignInResponse(accessToken,refreshToken);

    }

    @Override
    @Transactional(readOnly = true)
    public SignInResponse refresh(HttpServletRequest request) {
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new ApiException(ErrorStatus.INVALID_TOKEN,"유효하지 않은 리프레시 토큰 입니다");
        }
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String refreshTokenFromRedis = values.get(email);

        if (!refreshToken.equals(refreshTokenFromRedis)) {
            throw new ApiException(ErrorStatus.INVALID_TOKEN,"서버에 저장된 리프레시 토큰과 다릅니다");
        }

        Member user = memberRepository.getWithRoles(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER));

        String newAccessToken = createAccessToken(user);

        return new SignInResponse(newAccessToken,refreshToken);
    }


    @Override
    @Transactional
    public void logout(String token) {

        String email = jwtTokenProvider.getEmailFromToken(token);
        redisTemplate.delete(email);

    }



}

