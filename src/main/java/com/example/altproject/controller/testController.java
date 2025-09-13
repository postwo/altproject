package com.example.altproject.controller;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class testController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/test/global-exception")
    public String testGlobalException() {
        throw new RuntimeException("테스트용 일반 예외 발생!");
    }


    @GetMapping("/test/api-exception")
    public String testApiException() {
        throw new ApiException(ErrorStatus.AUTHORIZATION_FAIL, "테스트용 ApiException 발생!");
    }

    @GetMapping("/api/private/hello")
    public String privateHello() {
        return "인증된 사용자";
    }

    @GetMapping("/api/admin/dashboard")
    public String dashboard() {
        return "관리자 전용 대시보드";
    }

    @GetMapping("/api/admin/users")
    public List<Member> getAllUsers(){
        return memberRepository.findAll();
    }
}
