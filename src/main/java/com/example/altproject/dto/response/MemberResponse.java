package com.example.altproject.dto.response;

import com.example.altproject.domain.member.Member;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

    private Long id;
    private String email;
    private String nickname;

    public static MemberResponse from(Member member){
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
