package com.example.altproject.controller;

import com.example.altproject.common.api.ApiResponse;
import com.example.altproject.dto.request.SignInRequest;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.MemberResponse;
import com.example.altproject.dto.response.SignInResponse;
import com.example.altproject.dto.response.SignUpResponse;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.service.MemberService;
import com.example.altproject.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        SignUpResponse response = memberService.signUp(request);
        return ApiResponse.Success(response);
    }

    @PostMapping("/login")
    public ApiResponse<SignInResponse> signIn(@RequestBody @Valid SignInRequest request, HttpServletResponse response) {
        SignInResponse tokens = memberService.login(request);

        Cookie refreshCookie = new Cookie("refreshToken", tokens.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(refreshCookie);

        return ApiResponse.Success(tokens);
    }

    @PostMapping("/refresh")
    public ApiResponse<SignInResponse> refresh(HttpServletRequest request) {

        SignInResponse response = memberService.refresh(request);

        return ApiResponse.Success(response);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String accessToken,HttpServletResponse response) {
        String Token = accessToken.replace("Bearer ", "");
        memberService.logout(Token);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    @GetMapping("/me")
    public ApiResponse<MemberResponse> meUser(@AuthenticationPrincipal Object principal){
        MemberResponse response = memberService.getMemberInfo(principal);

        return ApiResponse.Success(response);
    }
}
