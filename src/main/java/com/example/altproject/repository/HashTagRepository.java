package com.example.altproject.repository;

import com.example.altproject.domain.hashtag.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    Optional<HashTag> findByHashtagName(String hashtagName);
}
