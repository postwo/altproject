package com.example.altproject.repository;

import com.example.altproject.domain.favorite.Favorite;
import com.example.altproject.domain.primarykey.FavoritePk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoritePk> {
    @Query("SELECT f FROM Favorite f WHERE f.board.id = :boardId AND f.member.email = :email")
    Favorite findByBoardIdAndMemberEmail(@Param("boardId") Long boardId, @Param("email") String email);

    boolean existsByBoardIdAndMemberEmail(Long boardId, String email);
}
