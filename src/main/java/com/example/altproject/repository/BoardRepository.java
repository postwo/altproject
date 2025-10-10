package com.example.altproject.repository;

import com.example.altproject.domain.board.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"images", "hashtags"})
    @Query("SELECT b FROM Board b ORDER BY b.id DESC") // JPQL로 쿼리 명시
    List<Board> findAllWithDetails();
}
