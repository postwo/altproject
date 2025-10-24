package com.example.altproject.chat.config;

import com.example.altproject.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
//StompHandler는 WebSocket으로 들어오는 모든 STOMP 관련 명령어(CONNECT, SUBSCRIBE, SEND 등)를 가로채서 처리하는 역할
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

        // 1. SecretKey 생성: Base64 디코딩된 키를 사용 (Provider와 동일하게 맞춰야 함)
        java.security.Key signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        /*클라이언트가 WebSocket에 연결할 때 실행됩니다.
        요청 헤더에서 JWT 토큰을 추출합니다.
        토큰의 유효성을 검증합니다 (만료 여부, 서명 검사 등).*/
        if(StompCommand.CONNECT == accessor.getCommand()){
            System.out.println("connect요청시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);

            try {
//            토큰 검증
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(signingKey)
                        .base64UrlDecodeWith(Decoders.BASE64URL)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                log.info("토큰 검증 성공");
                System.out.println("토큰 검증 완료");
            } catch (Exception e) {
                // 토큰 검증 실패 시 발생하는 모든 JWT 관련 예외를 잡아 로그로 출력
                log.error("JWT 토큰 검증 실패: {}", e.getMessage());
                // 예외를 발생시켜 연결을 거부 (클라이언트에게 에러를 알림)
                throw new AuthenticationServiceException("유효하지 않거나 만료된 토큰입니다.", e);
            }
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
            log.info("bearerToken = "+bearerToken);
            log.info("token2 = "+token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .base64UrlDecodeWith(Decoders.BASE64URL)
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
