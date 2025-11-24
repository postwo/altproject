package com.example.altproject.chat.controller;

import com.example.altproject.chat.dto.ChatMessageDto;
import com.example.altproject.chat.service.ChatService;
import com.example.altproject.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class StompController {

    private final ChatService chatService;
    private final RedisPubSubService pubSubService;

    public StompController(ChatService chatService, RedisPubSubService pubSubService) {
        this.chatService = chatService;
        this.pubSubService = pubSubService;
    }

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageReqDto) throws JsonProcessingException {

        // ⬇️ [추가] roomId 일치 여부 검증
        if (chatMessageReqDto.getRoomId() == null || !roomId.equals(chatMessageReqDto.getRoomId())) {
            // 여기서는 간단히 로그를 남기고 처리를 중단합니다.
            // 필요에 따라 특정 사용자에게 에러 메시지를 보내는 로직을 추가할 수 있습니다.
            System.err.println("Path의 roomId와 DTO의 roomId가 일치하지 않습니다. Path: " + roomId + ", DTO: " + chatMessageReqDto.getRoomId());
            return;
        }

        chatService.saveMessage(roomId, chatMessageReqDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(chatMessageReqDto);
        pubSubService.publish("chat", message);
    }
}
