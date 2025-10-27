package com.example.altproject.service;

import com.example.altproject.dto.request.SignInRequest;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.MemberResponse;
import com.example.altproject.dto.response.SignInResponse;
import com.example.altproject.dto.response.SignUpResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberService {

    SignUpResponse signUp(SignUpRequest request);
    SignInResponse login(SignInRequest request);

    SignInResponse refresh(HttpServletRequest request);

    void logout(String token);

    MemberResponse getMemberInfo(Object principal);

    MemberResponse updateNickname(Object principal,String nickname);
}
