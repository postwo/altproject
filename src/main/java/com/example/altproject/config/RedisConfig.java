package com.example.altproject.config;


import com.example.altproject.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory());
    }

    //    redis 연결기본객체
    //Redis 서버에 연결하기 위한 팩토리
    @Bean
    @Qualifier("chatPubSub") //@Qualifier("chatPubSub")의 "chatPubSub"은 개발자가 원하는 대로 변경할 수 있어요. 이는 빈을 구분하기 위한 식별자일 뿐
    public RedisConnectionFactory chatPubSubFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
//        redis pub/sub에서는 특정 데이터베이스에 의존적이지 않음.
//        configuration.setDatabase(0);
        return new LettuceConnectionFactory(configuration);
    }

    //    publish객체
    //Redis에 메시지를 발행(publish)하기 위한 메서드
    //publish:메시지를 특정 채널에 보내는 행위
    @Bean
    @Qualifier("chatPubSub")
//    일반적으로 RedisTemplate<key데이터타입, value데이터타입>을 사용
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory){
        return  new StringRedisTemplate(redisConnectionFactory);
    }

    //    subscribe객체
    /*역할: 특정 패턴의 토픽을 구독하고 메시지가 오면 처리
      동작: chat:으로 시작하는 모든 채널을 구독*/
    //subscribe:특정 채널의 메시지를 받겠다고 등록하는 행위
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter messageListenerAdapter
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic("chat"));
        return container;
    }

    //    redis에서 수신된 메시지를 처리하는 객체 생성
    //역할: Redis 메시지를 처리할 메서드와 연결
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService){
//        RedisPubSubService의 특정 메서드가 수신된 메시지를 처리할수 있도록 지정
        return new MessageListenerAdapter(redisPubSubService, "onMessage");

    }
}
