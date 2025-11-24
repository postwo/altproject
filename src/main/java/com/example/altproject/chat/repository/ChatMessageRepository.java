package com.example.altproject.chat.repository;

import com.example.altproject.chat.domain.ChatMessage;
import com.example.altproject.chat.domain.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    // 특정 채팅방의 메시지를 최신순으로 페이징하여 조회 (과거 메시지 불러오기용)
    List<ChatMessage> findByChatRoomIdOrderByIdDesc(Long chatRoomId, Pageable pageable);

    // 특정 채팅방에서 lastMessageId보다 큰 ID를 가진 메시지 수 카운트 (읽지 않은 메시지 수)
    long countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long lastMessageId);

}

