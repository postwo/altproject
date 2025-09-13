package com.example.altproject.service;

import com.example.altproject.dto.request.SignInRequest;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.SignInResponse;
import com.example.altproject.dto.response.SignUpResponse;

public interface MemberService {

    SignUpResponse signUp(SignUpRequest request);
    SignInResponse signIn(SignInRequest request);

    SignInResponse refresh(String token);

    void logout(String token);
}
