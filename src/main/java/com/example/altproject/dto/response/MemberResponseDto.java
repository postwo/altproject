package com.example.altproject.dto.response;

import com.example.altproject.domain.member.Member;
import com.example.altproject.domain.member.status.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private MemberStatus status;
    private long boardCount;
    private long chatRoomCount;

    public static MemberResponseDto from(Member member, long boardCount, long chatRoomCount) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .status(member.getStatus())
                .boardCount(boardCount)
                .chatRoomCount(chatRoomCount)
                .build();
    }
}
