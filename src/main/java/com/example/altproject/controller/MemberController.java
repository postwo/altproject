package com.example.altproject.controller;

import com.example.altproject.common.api.ApiResponse;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.SignUpResponse;
import com.example.altproject.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
