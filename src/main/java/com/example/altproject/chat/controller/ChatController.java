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
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

//    그룹채팅방 개설
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@RequestParam String roomName){
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }

//    그룹채팅목록조회
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatRooms(){
        List<ChatRoomListResDto> chatRooms = chatService.getGroupchatRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

//    그룹채팅방참여
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId){
        chatService.addParticipantToGroupChat(roomId);
        return ResponseEntity.ok().build();
    }

//    이전 메시지 조회
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId){
        List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(roomId);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }

//    채팅메시지 읽음처리
//    @PostMapping("/room/{roomId}/read")
//    public ResponseEntity<?> messageRead(@PathVariable Long roomId){
//        chatService.messageRead(roomId);
//        return ResponseEntity.ok().build();
//    }

//    내채팅방목록조회 : roomId, roomName, 그룹채팅여부, 메시지읽음개수
    @GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms(){
        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms();
        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);
    }

//    채팅방 나가기
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<?> leaveGroupChatRoom(@PathVariable Long roomId){
        chatService.leaveGroupChatRoom(roomId);
        return ResponseEntity.ok().build();
    }

//    개인 채팅방 개설 또는 기존roomId return
//    @PostMapping("/room/private/create")
//    public ResponseEntity<?> getOrCreatePrivateRoom(@RequestParam Long otherMemberId){
//        Long roomId = chatService.getOrCreatePrivateRoom(otherMemberId);
//        return new ResponseEntity<>(roomId, HttpStatus.OK);
//    }


    /**
     * 채팅방의 이전 메시지들을 조회합니다. (스크롤을 위로 올릴 때 사용)
     * @param roomId 채팅방 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 한 페이지에 가져올 메시지 수
     * @return 메시지 목록
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getPreviousMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ChatMessageDto> messages = chatService.getPreviousMessages(roomId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * 읽지 않은 메시지 수를 조회합니다. (채팅방 목록 뱃지에 사용)
     * @param roomId 채팅방 ID
     * @param lastMessageId 클라이언트가 마지막으로 읽은 메시지 ID
     * @return 읽지 않은 메시지 수
     */
    @GetMapping("/rooms/{roomId}/messages/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable Long roomId,
            @RequestParam Long lastMessageId) {
        long count = chatService.getUnreadMessageCount(roomId, lastMessageId);
        return ResponseEntity.ok(count);
    }
}
