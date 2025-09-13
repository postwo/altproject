package com.example.altproject.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponse {

    private String accessToken;
    private String refreshToken;

}
