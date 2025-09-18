package com.example.altproject.service;

import com.example.altproject.dto.request.SignInRequest;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.SignInResponse;
import com.example.altproject.dto.response.SignUpResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberService {

    SignUpResponse signUp(SignUpRequest request);
    SignInResponse login(SignInRequest request);

//    SignInResponse refresh(String token);

    SignInResponse refresh(HttpServletRequest request);

    void logout(String token);
}
