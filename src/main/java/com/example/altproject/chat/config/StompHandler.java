package com.example.altproject.chat.config;

import com.example.altproject.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;
    private final ChatService chatService;

    /*동작 흐름
    클라이언트가 WebSocket에 연결하면 CONNECT 명령이 전달되고, 토큰 검증이 이루어집니다.
    클라이언트가 특정 채팅방을 구독하면 SUBSCRIBE 명령이 전달되고, 해당 사용자가 채팅방에 참여 중인지 확인합니다.
    모든 검증이 통과하면 메시지가 정상적으로 전달됩니다.*/

    //WebSocket 메시지가 전송되기 전에 호출되는 메서드로, 클라이언트의 연결 및 구독 요청을 가로채서 처리
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        /*클라이언트가 WebSocket에 연결할 때 실행됩니다.
        요청 헤더에서 JWT 토큰을 추출합니다.
        토큰의 유효성을 검증합니다 (만료 여부, 서명 검사 등).*/
        if(StompCommand.CONNECT == accessor.getCommand()){
            System.out.println("connect요청시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);
//            토큰 검증
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("토큰 검증 완료");
        }

        //구독(SUBSCRIBE)은 클라이언트가 특정 채팅방의 메시지를 수신하겠다는 의미
        /*클라이언트가 특정 채팅방을 구독할 때 실행됩니다.
        토큰에서 사용자 이메일을 추출합니다.
        구독하려는 채팅방 ID를 추출합니다.
        ChatService를 통해 해당 사용자가 채팅방에 참여 중인지 확인합니다.
        권한이 없으면 예외를 발생시켜 구독을 거부합니다.*/
        if(StompCommand.SUBSCRIBE == accessor.getCommand()){
            System.out.println("subscribe 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String email = claims.getSubject();
            String roomId = accessor.getDestination().split("/")[2];
            if(!chatService.isRoomPaticipant(email, Long.parseLong(roomId))){
                throw new AuthenticationServiceException("해당 room에 권한이 없습니다.");
            }
        }

        return message;
    }

}
