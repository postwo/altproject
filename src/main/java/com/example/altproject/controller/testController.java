package com.example.altproject.controller;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class testController {


    @GetMapping("/test/global-exception")
    public String testGlobalException() {
        throw new RuntimeException("테스트용 일반 예외 발생!");
    }


    @GetMapping("/test/api-exception")
    public String testApiException() {
        throw new ApiException(ErrorStatus.AUTHORIZATION_FAIL, "테스트용 ApiException 발생!");
    }
}
