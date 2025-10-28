package com.example.altproject.dto.response;

import com.example.altproject.domain.board.BoardReport;
import com.example.altproject.domain.board.ReportReason;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardReportResponse {

    private Long reportId;              // 신고 내역의 고유 ID
    private String reporterNickname;    // 신고한 사용자의 닉네임

    private Long reportedBoardId;       // 신고된 게시글의 ID
    private String reportedBoardTitle;  // 신고된 게시글의 제목

    private ReportReason reason;            // 신고 사유 (Enum 값)
    private String reasonDescription;   // 신고 사유 (한글 설명)
    private String details;             // 신고 상세 내용

    private String reportedAt;          // 신고된 시간

    /**
     * BoardReport 엔티티를 BoardReportResponse DTO로 변환하는 정적 팩토리 메소드입니다.
     * @param report 조회된 BoardReport 엔티티
     * @return 변환된 DTO
     */
    public static BoardReportResponse from(BoardReport report) {
        return BoardReportResponse.builder()
                .reportId(report.getId())
                .reporterNickname(report.getReporter().getNickname()) // 신고자 정보
                .reportedBoardId(report.getReportedBoard().getId())       // 신고된 게시글 정보
                .reportedBoardTitle(report.getReportedBoard().getTitle())
                .reason(report.getReason())
                .reasonDescription(report.getReason().getDescription()) // Enum의 한글 설명
                .details(report.getDetails())
                .reportedAt(report.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }
}