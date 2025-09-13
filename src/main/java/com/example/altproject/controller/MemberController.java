package com.example.altproject.controller;

import com.example.altproject.common.api.ApiResponse;
import com.example.altproject.dto.request.SignInRequest;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.SignInResponse;
import com.example.altproject.dto.response.SignUpResponse;
import com.example.altproject.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        SignUpResponse response = memberService.signUp(request);
        return ApiResponse.Success(response);
    }

    @PostMapping("/signin")
    public ApiResponse<SignInResponse> signIn(@RequestBody @Valid SignInRequest request) {
        SignInResponse response = memberService.signIn(request);
        return ApiResponse.Success(response);
    }

    @PostMapping("/refresh")
    public ApiResponse<SignInResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        String Token = refreshToken.replace("Bearer ", "");

        SignInResponse response = memberService.refresh(Token);

        return ApiResponse.Success(response);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String accessToken) {
        String Token = accessToken.replace("Bearer ", "");
        memberService.logout(Token);
    }

}
