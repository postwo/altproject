package com.example.altproject.chat.repository;


import com.example.altproject.chat.domain.ChatRoom;
import com.example.altproject.chat.domain.ReadStatus;
import com.example.altproject.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {
    List<ReadStatus> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
    Long countByChatRoomAndMemberAndIsReadFalse(ChatRoom chatRoom, Member member);
}
