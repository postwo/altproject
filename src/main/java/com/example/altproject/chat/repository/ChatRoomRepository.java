package com.example.altproject.chat.repository;

import com.example.altproject.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByIsGroupChat(String isGroupChat);

    Optional<ChatRoom> findByName(String title);

    void deleteByName(String name);
}
