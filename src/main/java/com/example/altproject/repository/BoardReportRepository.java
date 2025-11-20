package com.example.altproject.repository;

import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.board.BoardReport;
import com.example.altproject.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReportRepository extends JpaRepository<BoardReport, Long> {
    // 특정 사용자가 특정 게시글을 이미 신고했는지 확인하기 위한 메소드
    boolean existsByReporterAndReportedBoard(Member reporter, Board reportedBoard);
    
    // 게시글에 대한 모든 신고 내역 삭제
    void deleteByReportedBoard(Board reportedBoard);
}
