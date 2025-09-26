package com.example.altproject.repository;

import com.example.altproject.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByBoardId(Long boardId);

    void deleteByBoardId(Long boardId);
}
