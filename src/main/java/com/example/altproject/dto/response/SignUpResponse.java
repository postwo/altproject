package com.example.altproject.dto.response;

import com.example.altproject.domain.member.Member;
import com.example.altproject.domain.member.status.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {
    private Long id;
    private String email;
    private String nickname;
    private UserStatus role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static SignUpResponse responseMember(Member member) {
        return SignUpResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
}