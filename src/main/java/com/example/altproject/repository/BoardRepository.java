package com.example.altproject.repository;

import com.example.altproject.domain.board.Board;
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
}
