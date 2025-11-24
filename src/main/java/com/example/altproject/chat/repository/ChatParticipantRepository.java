package com.example.altproject.chat.repository;

import com.example.altproject.chat.domain.ChatParticipant;
import com.example.altproject.chat.domain.ChatRoom;
import com.example.altproject.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
    List<ChatParticipant> findAllByMember(Member member);

    @Query("SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 ON cp1.chatRoom.id = cp2.chatRoom.id WHERE cp1.member.id = :myId AND cp2.member.id = :otherMemberId AND cp1.chatRoom.isGroupChat = 'N'")
    Optional<ChatRoom> findExistingPrivateRoom(@Param("myId") Long myId, @Param("otherMemberId") Long otherMemberId);

    List<ChatParticipant> findByMember(Member member);

    long countByMember(Member member);

    // ⬇️ [추가] 특정 채팅방에 특정 멤버가 존재하는지 여부만 확인하는 효율적인 쿼리
    boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
