package com.example.altproject.domain.board;


import lombok.Getter;

@Getter // 각 Enum 상수가 자신의 설명(description)을 가질 수 있도록 Getter를 추가합니다.
public enum ReportReason {

    // 여기에 신고 사유의 종류를 미리 정의합니다.
    SPAM("스팸/홍보성 게시물"),
    INAPPROPRIATE_CONTENT("음란물 또는 불건전한 만남 및 대화"),
    ABUSE("욕설, 비하, 혐오 발언"),
    SCAM("사기 또는 사칭 행위"),
    OTHER("기타 (상세 사유 작성 필요)");

    // 각 Enum 상수가 한글 설명 값을 가질 수 있도록 필드를 추가합니다.
    private final String description;

    // 생성자: 각 Enum 상수가 생성될 때, 자신의 설명 값을 자동으로 할당받습니다.
    ReportReason(String description) {
        this.description = description;
    }
}
