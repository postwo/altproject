package com.example.altproject.dto.request;

import com.example.altproject.domain.board.ReportReason;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardReportRequest {
    private ReportReason reason; // 신고 사유 (Enum)
    private String details;      // 상세 내용
}
