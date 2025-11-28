package com.example.altproject.chat.controller;


import com.example.altproject.chat.dto.ChatMessageDto;
import com.example.altproject.chat.dto.ChatRoomListResDto;
import com.example.altproject.chat.dto.MyChatListResDto;
import com.example.altproject.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat") // ⬇️ [수정] 경로 일관성을 위해 /api 추가
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

//    그룹채팅방 개설
    @PostMapping("/room/group/create")
    public ResponseEntity<Long> createGroupRoom(@RequestParam String roomName,@RequestParam Long boardId){
        Long roomId = chatService.createGroupRoom(roomName,boardId);
        return ResponseEntity.ok(roomId);
    }

//    그룹채팅목록조회
    @GetMapping("/room/group/list")
    public ResponseEntity<List<ChatRoomListResDto>> getGroupChatRooms(){
        List<ChatRoomListResDto> chatRooms = chatService.getGroupchatRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

//    그룹채팅방참여
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<Void> joinGroupChatRoom(@PathVariable Long roomId){
        chatService.addParticipantToGroupChat(roomId);
        return ResponseEntity.ok().build();
    }

//    채팅메시지 읽음처리
    @PutMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> messageRead(@PathVariable Long roomId){
        chatService.messageRead(roomId);
        return ResponseEntity.ok().build();
    }

//    내채팅방목록조회 : roomId, roomName, 그룹채팅여부, 메시지읽음개수
    @GetMapping("/my/rooms")
    public ResponseEntity<List<MyChatListResDto>> getMyChatRooms(){
        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms();
        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);
    }

//    채팅방 나가기
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<Void> leaveGroupChatRoom(@PathVariable Long roomId){
        chatService.leaveGroupChatRoom(roomId);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방의 모든 메시지를 조회합니다.
     * @param roomId 채팅방 ID
     * @return 메시지 목록
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getAllMessages(
            @PathVariable Long roomId) {
        List<ChatMessageDto> messages = chatService.getAllMessages(roomId);
        return ResponseEntity.ok(messages);
    }

    // ⬇️ [추가] 로그인한 사용자의 전체 안 읽은 메시지 수를 조회 (헤더 알림 뱃지용)
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Long> getTotalUnreadMessageCount() {
        long count = chatService.getTotalUnreadMessageCount();
        return ResponseEntity.ok(count);
    }
}
