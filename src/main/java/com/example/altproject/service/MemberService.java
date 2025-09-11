package com.example.altproject.service;

import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.SignUpResponse;

public interface MemberService {

    SignUpResponse signUp(SignUpRequest request);
}
