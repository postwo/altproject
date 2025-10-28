package com.example.altproject.admin.service;

import com.example.altproject.domain.board.BoardReport;
import com.example.altproject.dto.response.BoardReportResponse;
import com.example.altproject.repository.BoardReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final BoardReportRepository boardReportRepository;

    /**
     * 모든 게시글 신고 내역을 조회하여 DTO 리스트로 반환합니다.
     * @return BoardReportResponse DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<BoardReportResponse> getAllBoardReports() {
        // 1. 데이터베이스에서 모든 신고 내역을 조회합니다.
        List<BoardReport> reports = boardReportRepository.findAll();

        // 2. 조회된 엔티티 리스트를 스트림을 사용하여 DTO 리스트로 변환합니다.
        return reports.stream()
                .map(BoardReportResponse::from) // 정적 팩토리 메소드 호출
                .collect(Collectors.toList());
    }
}
