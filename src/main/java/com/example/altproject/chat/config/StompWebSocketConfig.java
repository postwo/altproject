package com.example.altproject.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    /* 프론트 연결 예제
    // Step 1: WebSocket 연결 시도
    const socket = new SockJS('http://localhost:8080/connect');
    const stompClient = Stomp.over(socket);

    stompClient.connect(
        {Authorization: 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'},
        (frame) => {
            console.log('연결 성공!', frame);
        }
    );
    * */
    // 백엔드에서 일어나는 일:
    //1. 클라이언트가 http://localhost:8080/connect 로 연결 요청
    //2. StompHandler의 preSend 메서드가 실행됨 (JWT 토큰 검증)
    //3. 연결 성공 시 WebSocket 세션 생성
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect") // ✅ 원하는 이름으로 변경 가능 //클라이언트에서 WebSocket 연결 시 이 경로로 접속해야 함
                .setAllowedOrigins("http://localhost:5173")
//                ws://가 아닌 http:// 엔드포인트를 사용할수 있게 해주는 sockJs라이브러리를 통한 요청을 허용하는 설정.
                .withSockJS();
    }


    /*
    // Step 2: 1번 채팅방 구독 (메시지 받기 준비)
    stompClient.subscribe('/topic/chat/room/1', (message) => {
        const receivedMsg = JSON.parse(message.body);
        console.log('받은 메시지:', receivedMsg);
        // 화면에 메시지 표시
        displayMessage(receivedMsg);
    });

    백엔드 동작:

    1. 클라이언트가 "/topic/chat/room/1" 구독 요청
    2. StompHandler에서 SUBSCRIBE 이벤트 감지
    3. 해당 채팅방(room/1)을 구독하는 클라이언트 목록에 추가
    4. 이제 이 클라이언트는 "/topic/chat/room/1"로 발행되는 모든 메시지를 받게 됨
    */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        /publish/1형태로 메시지 발행해야 함을 설정
//        /publish로 시작하는 url패턴으로 메시지가 발행되면 @Controller 객체의 @MessaMapping메서드로 라우팅
        //클라이언트가 서버의 @MessageMapping으로 메시지를 보낼 때 사용하는 prefix
        registry.setApplicationDestinationPrefixes("/publish"); // ✅ 원하는 이름으로 변경 가능

//        /topic/1형태로 메시지를 수신(subscribe)해야 함을 설정
        //클라이언트가 메시지를 구독(subscribe)할 때 사용하는 prefix
        registry.enableSimpleBroker("/topic"); // ✅ 원하는 이름으로 변경 가능

    }


    //    웹소켓요청(connect, subscribe, disconnect)등의 요청시에는 http header등 http메시지를 넣어올수 있고, 이를 interceptor를 통해 가로채 토큰등을 검증할수 있음.
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

}
