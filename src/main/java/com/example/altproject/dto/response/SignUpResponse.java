package com.example.altproject.dto.response;

import com.example.altproject.domain.member.Member;
import com.example.altproject.domain.member.status.MemberRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {
    private Long id;
    private String email;
    private String nickname;
    private List<MemberRole> roles;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static SignUpResponse responseMember(Member member) {
        return SignUpResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .roles(member.getMemberRoleList())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
}