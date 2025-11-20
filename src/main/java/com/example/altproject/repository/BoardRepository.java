package com.example.altproject.repository;

import com.example.altproject.domain.board.Board;
import com.example.altproject.dto.response.BoardResponse;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"images", "hashtags"})
    @Query("SELECT b FROM Board b ORDER BY b.id DESC") // JPQL로 쿼리 명시
    List<Board> findAllWithDetails();

    // 상세 조회 시 N+1 문제를 방지하기 위한 Fetch Join 쿼리 추가
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.images LEFT JOIN FETCH b.hashtags WHERE b.id = :boardId")
    Optional<Board> findByIdWithDetails(@Param("boardId") Long boardId);

    List<Board> findByWriterEmailOrderByCreatedAtDesc(String email);

    List<Board> findByTitleIn(List<String> titles);

    long countByWriterEmail(String email);

    // 관리자 페이지용: 모든 게시글 정보와 신고 횟수를 함께 조회 (수정된 쿼리)
    @Query("SELECT new com.example.altproject.dto.response.BoardResponse(b.id, b.title, b.content, b.address, b.totalPrice, b.maxParticipants, b.writerEmail, b.createdAt, COUNT(br.id)) " +
           "FROM Board b LEFT JOIN b.reports br " + // Board 엔티티의 연관관계 필드를 직접 사용
           "GROUP BY b.id, b.title, b.content, b.address, b.totalPrice, b.maxParticipants, b.writerEmail, b.createdAt " +
           "ORDER BY b.createdAt DESC")
    List<BoardResponse> findAllWithReportCount();

    // 해시태그 정보를 함께 조회하기 위한 메서드
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.hashtags WHERE b.id IN :boardIds")
    List<Board> findAllByIdInWithHashtags(@Param("boardIds") List<Long> boardIds);
}
