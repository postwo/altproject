package com.example.altproject.chat.repository;


import com.example.altproject.chat.domain.ChatRoom;
import com.example.altproject.chat.domain.ReadStatus;
import com.example.altproject.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {
    List<ReadStatus> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
    Long countByChatRoomAndMemberAndIsReadFalse(ChatRoom chatRoom, Member member);

    // ⬇️ [수정] 사용자를 기준으로 읽지 않은 모든 메시지의 수를 계산하는 메소드
    long countByMemberAndIsReadFalse(Member member);

    // ⬇️ [추가] N+1 문제 해결을 위한 쿼리
    @Query("SELECT rs.chatRoom as chatRoom, COUNT(rs) as count " +
            "FROM ReadStatus rs " +
            "WHERE rs.chatRoom IN :chatRooms AND rs.member = :member AND rs.isRead = false " +
            "GROUP BY rs.chatRoom")
    List<UnreadCount> countByChatRoomInAndMemberAndIsReadFalse(@Param("chatRooms") List<ChatRoom> chatRooms, @Param("member") Member member);

    // ⬇️ [추가] 위 쿼리의 결과를 담을 인터페이스
    interface UnreadCount {
        ChatRoom getChatRoom();
        Long getCount();
    }
}
