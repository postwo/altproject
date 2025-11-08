# 채팅 시퀀스 회로
https://mermaid.live 여기 들어가서 확인가능

sequenceDiagram
participant Client
participant StompBroker
participant StompController
participant ChatService
participant RedisPubSubService
participant Redis
participant RedisSubscriber
participant SimpMessageSendingOperations

    Client->>+StompBroker: 1. CONNECT
    StompBroker-->>-Client: 2. CONNECTED

    Client->>+StompBroker: 3. SUBSCRIBE /topic/{roomId}
    StompBroker-->>-Client: 4. SUBSCRIBED

    Client->>+StompBroker: 5. SEND /app/{roomId} (ChatMessageDto)
    StompBroker->>+StompController: 6. sendMessage(roomId, chatMessageDto)
    StompController->>+ChatService: 7. saveMessage(roomId, chatMessageDto)
    ChatService-->>-StompController: 8. 메시지 저장 완료
    StompController->>+RedisPubSubService: 9. publish("chat", message)
    RedisPubSubService->>+Redis: 10. PUBLISH chat "message"
    Redis-->>-RedisPubSubService: 11. 발행 완료
    RedisPubSubService-->>-StompController: 12. 발행 완료
    StompController-->>-StompBroker: 13. 처리 완료

    Redis->>+RedisSubscriber: 14. onMessage("chat", "message")
    RedisSubscriber->>+SimpMessageSendingOperations: 15. convertAndSend("/topic/"+roomId, chatMessageDto)
    SimpMessageSendingOperations->>+StompBroker: 16. MESSAGE /topic/{roomId}
    StompBroker-->>-RedisSubscriber: 17. 전송 완료
    RedisSubscriber-->>-Redis: 18. 처리 완료

    StompBroker->>Client: 19. MESSAGE /topic/{roomId} (ChatMessageDto)

# 채팅 설명
1. 연결 및 구독 단계
   •
   Client → StompBroker: 사용자가 채팅방에 들어가면, 먼저 서버의 StompBroker와 웹소켓(WebSocket) 연결을 맺습니다.
   •
   Client → StompBroker: 연결이 성공하면, Client는 특정 채팅방(roomId)에 오는 메시지를 받기 위해 /topic/{roomId}라는 주소를 **구독(Subscribe)**합니다. 이제 이 주소로 오는 모든 메시지를 수신할 준비가 된 상태입니다.
2. 메시지 전송 및 저장 단계
   •
   Client → StompBroker: 사용자가 메시지를 입력하고 전송하면, 이 메시지(ChatMessageDto)는 /app/{roomId}라는 주소로 보내집니다.
   •
   StompBroker → StompController: StompBroker는 /app/{roomId}로 들어온 메시지를 보고, 이를 처리할 StompController의 sendMessage 메소드를 호출합니다.
   •
   StompController → ChatService: StompController는 ChatService를 호출하여, 받은 메시지를 데이터베이스에 저장합니다. 이는 대화 기록을 남기기 위함입니다.
3. 메시지 발행(Publish) 단계
   •
   StompController → RedisPubSubService: 메시지 저장이 끝나면, StompController는 RedisPubSubService를 통해 메시지를 Redis의 "chat" 채널로 **발행(Publish)**합니다.
   •
   RedisPubSubService → Redis: 서비스는 받은 메시지를 Redis 서버로 전달하여 "chat" 채널에 등록된 모든 구독자에게 알릴 준비를 합니다.
   (핵심 이유: 만약 서버가 여러 대(Scale-out)라면, A 서버에 접속한 사용자의 메시지를 B 서버에 접속한 사용자에게 전달해야 합니다. Redis가 이 중간 다리 역할을 합니다.)
4. 메시지 구독 및 재전송 단계
   •
   Redis → RedisSubscriber: Redis는 "chat" 채널로 메시지가 들어왔음을 모든 구독자(RedisSubscriber)에게 알립니다.
   •
   RedisSubscriber → SimpMessageSendingOperations: 메시지를 받은 RedisSubscriber는 SimpMessageSendingOperations라는 메시지 전송 도구를 사용하여, 이 메시지를 다시 웹소켓 브로커에게 보낼 준비를 합니다. 보낼 주소는 맨 처음 사용자가 구독했던 /topic/{roomId} 입니다.
   •
   SimpMessageSendingOperations → StompBroker: 메시지 전송 도구는 StompBroker에게 "이 메시지를 /topic/{roomId} 주소로 보내줘" 라고 요청합니다.
5. 최종 메시지 수신 단계
   •
   StompBroker → Client: StompBroker는 /topic/{roomId} 주소를 구독하고 있던 모든 Client에게 최종적으로 메시지를 전달합니다.
   •
   결과: 채팅방에 참여한 모든 사용자의 화면에 새로운 메시지가 표시됩니다.


# @RequestParam (쿼리 파라미터)
예시 1: 특정 게시글 조회게시글 번호($\text{id}$)를 이용해 특정 게시글을 조회할 때
게시글 번호($\text{id}$)를 이용해 특정 게시글을 조회할 때URL: /api/posts/105용도: 여기서 $\text{105}$는 105번 게시글이라는 특정 리소스를 식별하는 값입니다.

예시 2: 특정 사용자 프로필 조회
사용자의 아이디나 이메일($\text{email}$)을 경로로 받아 해당 사용자 정보를 조회할 때URL: /api/users/user@example.com

# @PathVariable
예시 1: 게시글 목록 검색 및 페이징
게시글 목록을 조회할 때 **검색어($\text{keyword}$)**와 **페이지 번호($\text{page}$)**를 전달할 때URL: /api/posts?keyword=자바&page=2

예시 2: 데이터 필터링사용자 목록에서 활성 상태($\text{status}$)인 사용자만 필터링할 때URL: /api/users?status=active
# 다이어그램 확인 방법
1. GitHub에서 보기
   파일을 커밋하고 GitHub에 푸시하면 Mermaid 다이어그램이 자동으로 렌더링됩니다.
2. IntelliJ IDEA에서 보기
   IntelliJ에 Mermaid 플러그인 설치:
   Settings → Plugins → "Mermaid" 검색 → 설치
   파일을 열면 미리보기가 가능합니다.
3. VS Code에서 보기
   Markdown Preview Mermaid Support 확장 설치
   Ctrl+Shift+V로 미리보기


# 아키텍처 다이어그램 확인사이트
https://mermaid.live/edit#pako:eNpVjbFugzAQhl_FuqmVSISdAImHSg1ps0Rqh0yFDCdwMEqwkTFKU-Dda4iqtjfd6fv-_zrIdC6Aw-mir5lEY8lhmyri5jmJpSkbW2FzJLPZU78TllRaiVtPNg87TRqp67pUxePd34wSibv9qAliZanOwx3FU_5NiZ5skz3WVtfHv-Rw1T15Scp36er_E2mES70mJ-QnnGVoSIxmUsCDwpQ5cGta4UElTIXjCd1IU7BSVCIF7tYczTmFVA0uU6P60Lr6iRndFhJc96VxV1vnaMW2xMLgryJULkysW2WBMzZVAO_gE_hixeZhGEQBXS9XfsSoBzfgYTiPoiDwQ0p9uvYpGzz4mn768xX1F8GC0WUUsZAxOnwDJDp1Uw

https://mermaid.live

# 확인사이트에서 이거 붙여 넣기 
graph TB
subgraph Client["Client"]
WEB["🌐 Web Browser"]
MOBILE["📱 Mobile App"]
end

    WEB --> |"HTTP Request"| CONTROLLER
    MOBILE --> |"REST API"| CONTROLLER

    subgraph Backend["Spring Boot Application"]
        CONTROLLER["🎮 Controllers<br/>(MemberController,<br/>BoardController)"]
        
        subgraph Services["Service Layer"]
            MEMBER_S["👤 Member Service"]
            BOARD_S["📝 Board Service"]
            IMAGE_S["🖼️ Image Service"]
        end
        
        CONTROLLER --> |"Business Logic"| MEMBER_S
        CONTROLLER --> |"Business Logic"| BOARD_S
        BOARD_S --> |"Upload/Delete"| IMAGE_S
        
        subgraph Repositories["Repository Layer"]
            MEMBER_R["MemberRepository"]
            BOARD_R["BoardRepository"]
            IMAGE_R["ImageRepository"]
        end
        
        MEMBER_S --> |"Data Access"| MEMBER_R
        BOARD_S --> |"Data Access"| BOARD_R
        IMAGE_S --> |"Data Access"| IMAGE_R
    end

    subgraph Security["🔒 Security Layer"]
        JWT["JWT Filter"]
        OAUTH["OAuth2 Handler"]
    end

    CONTROLLER -.-> |"Authentication"| JWT
    CONTROLLER -.-> |"Social Login"| OAUTH

    subgraph Storage["Data Storage"]
        DB[("💾 RDBMS<br/>(MySQL/PostgreSQL)")]
        REDIS[("⚡ Redis<br/>(Cache & Session)")]
        FILE["📁 File Storage<br/>(Images)"]
    end

    MEMBER_R --> |"Save/Find User"| DB
    BOARD_R --> |"Save/Find Board"| DB
    IMAGE_R --> |"Save Metadata"| DB
    IMAGE_S --> |"Upload Files"| FILE
    
    MEMBER_S --> |"일반 로그인<br/>Token Store"| REDIS
    OAUTH --> |"소셜 로그인<br/>Token Store"| REDIS
    
    subgraph Admin["관리자"]
        ADMIN["👨‍💼 Admin"]
    end
    
    ADMIN --> |"Manage Users/Posts"| CONTROLLER

    style Client fill:#e3f2fd
    style Backend fill:#f3e5f5
    style Security fill:#ffebee
    style Storage fill:#e8f5e9
    style Admin fill:#fff3e0

# 여기 코드에 대해 예를 만들어서 설며해줘

# use case
![알뜰모아 최종 유즈케이스.jpg](image%2F%EC%95%8C%EB%9C%B0%EB%AA%A8%EC%95%84%20%EC%B5%9C%EC%A2%85%20%EC%9C%A0%EC%A6%88%EC%BC%80%EC%9D%B4%EC%8A%A4.jpg)

swagger 젒고 경로
http://localhost:8080/swagger-ui/index.html

예시 컨트롤러 만들기

JWT 인증을 거친 후, 인증된 사용자가 내 프로필 보기 API를 호출한다고 가정해봅시다.

@RestController
@RequestMapping("/api/members")
public class MemberController {

    // 인증된 사용자 정보 가져오기 (Spring Security에서 주입해줌)
    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile(Authentication authentication) {
        // JwtAuthenticationFilter에서 SecurityContextHolder에 넣어둔 정보가 여기서 꺼내짐
        String email = authentication.getName(); // 토큰에서 꺼낸 email
        return ResponseEntity.ok("현재 로그인한 사용자: " + email);
    }
}

흐름 설명

클라이언트 요청

GET /api/members/me
Authorization: Bearer eyJhbGciOiJIUzI1Ni...


JwtAuthenticationFilter 동작

resolveToken() → 토큰 꺼냄

jwtTokenProvider.validateToken(token) → 토큰 검증

jwtTokenProvider.getEmailFromToken(token) → email 추출 (test@example.com)

memberRepository.findByEmail(email) → DB에서 사용자 찾음

UserDetailsService.loadUserByUsername(email) → UserDetails 생성

SecurityContextHolder.getContext().setAuthentication(authenticationToken) → 인증 정보 저장

filterChain.doFilter(request, response)

여기서 컨트롤러로 요청이 넘어감

이제 SecurityContextHolder에 Authentication이 있으니, 컨트롤러에서 주입받을 수 있음

컨트롤러 실행

getMyProfile(Authentication authentication) 호출

authentication.getName() → "test@example.com" 반환

응답:

"현재 로그인한 사용자: test@example.com"




# 정리
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
Member member = memberRepository.getWithRoles(email)
.orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER,"사용자를 찾을 수 없습니다: " + email));
return new CustomUserDetails(member);
}
여기서 객체를 생성하면 

여기에 매개변수 member 가 클래스 내부의 private final Member member 필드에 저장
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getMemberRoleList().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }


# 위 내용 정리
@RequiredArgsConstructor 때문에 final 필드인 member를 매개변수로 받는 생성자가 자동으로 만들어집니다.

따라서 new CustomUserDetails(member)를 호출하면, 전달한 member 객체가 그대로 클래스 내부의 private final Member member 필드에 저장됩니다.

그 이후 getUsername(), getPassword(), getAuthorities() 등의 메서드는 저장된 member 객체에서 데이터를 꺼내 반환합니다.




@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "board_list_view")
@Table(name = "board_list_view")
public class BoardListViewEntity { //뷰테이블은 entity 타입을 지정할 필요 없다

    @Id
    private int boardNumber;
    private String title;
    private String content;
    private String titleImage; //I
    private int viewCount;
    private int favoriteCount;
    private int commentCount;
    private String writeDatetime;
    private String writerEmail; //U
    private String writerNickname; //U
    private String writerProfileImage; //U
}

# 중간 테이블을 board에 단이유
@ToString.Exclude
@JoinTable(
name = "board_hashtag",
joinColumns = @JoinColumn(name = "boardId"),
inverseJoinColumns = @JoinColumn(name = "hashtagId")
)
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private Set<HashTag> hashtags = new LinkedHashSet<>();

쉽게 말하면:

게시글(Board) 이 메인(부모) → 그 아래에 여러 개의 해시태그(HashTag) 가 붙는 구조.

그래서 게시글이 주인이 되면, "이 글에 어떤 해시태그를 달겠다" 라고 관리하는 게 자연스럽습니다.

글 작성할 때 → 해시태그를 붙이고 저장

글 수정할 때 → 해시태그를 빼거나 추가

👉 이런 흐름이 훨씬 현실적인 사용 방식이니까 Board 쪽에 @JoinTable을 두고 주인으로 두는 거예요

# 조인 테이블
@Builder.Default
@Enumerated(EnumType.STRING)
@ElementCollection(fetch = FetchType.LAZY)
private List<MemberRole> memberRoleList = new ArrayList<>();

이렇게 하면 MemberRole 이 Enum이고, @ElementCollection 을 통해 별도의 
엔티티를 두지 않고도 Member ↔ Role 매핑용 테이블이 자동으로 만들어집니다

동작 방식

JPA는 @ElementCollection 이 붙은 필드를 위해 별도의 컬렉션 테이블을 생성합니다.

예를 들어 Member 엔티티에 위 코드가 있으면 아래와 같은 보조 테이블이 자동 생성됩니다:

create table member_member_role_list (
member_id bigint not null,
memberRoleList varchar(255)
);


# 테이블 이름 변경
@Entity
public class Member {
@ElementCollection
private List<MemberRole> memberRoleList;
}
👉 member_member_role_list 테이블이 만들어진 거예요.

member : 엔티티 이름

memberRoleList : 필드 이름

중간에 _ 붙여서 member_member_role_list

@Builder.Default
@Enumerated(EnumType.STRING)
@ElementCollection(fetch = FetchType.LAZY)
@CollectionTable(
name = "member_roles",                  // 테이블명 변경
joinColumns = @JoinColumn(name = "member_id") // 조인 컬럼명 변경
)
@Column(name = "role")                      // 컬럼명 변경
private List<MemberRole> memberRoleList = new ArrayList<>();

이렇게 하면 DDL은 👇
create table member_roles (
member_id bigint not null,
role varchar(255)
);

# ㅐoauth2user,userdetails 한번에 구현하기
public class CustomOAuth2User implements OAuth2User, UserDetails  이런형식으로 

# 카카오 이메일 필수 신청 방법
앱 들어가서 앱아이콘등록 -> 개인 개발자 추가 상항 선택 -> 카카오 로그인 -> 동의항목 들어가서 활성화 시켜주면 된다

# oauth 로그인 토큰과 successhandler 처리
oauth로그인을 서버에서 처리할거면 
kakao 같은경우는 플랫폼(web)(서버포트 ex) http://localhost:8080),
카카오 로그인 (일반)(리다이렉트 uri를 서버포트랑 같게 해야한다 ex) http://localhost:8080/oauth2/callback/kakao)
이렇게 해야지만 suceesshandler 가 동작을 한다 그리고 리다이렉트 uri에서 포트와 callback 부분은 마음대로 수정한다
만약에 프론트에서 처리할거면 프론트 포트로 변경하면 된다 
정리하자면 서버에서 처리할거면 서버포트하고 같게 해주면 된다 


# @Configuration vs @Component
@Component: 특정 계층에 속하지 않는 범용적인 Spring 빈에 사용됩니다. 
이 클래스의 인스턴스를 빈으로 등록하고 싶을 때 사용합니다.

@Configuration: 여러 빈들을 한곳에 모아 정의하는 용도로 사용됩니다.
@Configuration이 붙은 클래스 내부의 @Bean 메서드들은 Spring 컨테이너에 의해 
프록시로 감싸져서 싱글톤 보장과 같은 특별한 처리를 받습니다.

# auditing 한계
AuditingFields의 한계점
AuditingFields에 있는 createdBy와 modifiedBy는 단지 String 타입의 기록일 뿐입니다. 이 정보만으로는 다음과 같은 중요한 비즈니스 로직을 처리할 수 없습니다.

게시글과 회원의 관계: String 형태의 createdBy는 단순히 "사용자명"이나 "이메일"과 같은 문자열입니다. 이 문자열은 실제 Member 엔티티 객체와 연결되어 있지 않습니다. 
따라서 이 게시글을 쓴 회원의 프로필 사진, 이름, 혹은 다른 정보가 필요할 때 createdBy만으로는 조회할 수 없습니다.

게시글 소유권 확인: @LastModifiedBy나 @CreatedBy는 수정 권한을 확인하는 데 적합하지 않습니다. 예를 들어, 게시글을 수정하려 할 때, 
현재 로그인한 사용자가 createdBy와 일치하는지 확인하는 로직을 직접 구현해야 하는데, 이 과정이 매우 번거롭고 안전하지 않습니다.

# 채팅
. 공동구매와 같이 게시글을 기반으로 다수의 인원이 소통해야 하는 경우에는 게시글 작성 완료 시 채팅방을 개설하도록 설계하는 것이 가장 효율적
동구매 게시글을 작성하는 시점에 그룹 채팅방을 즉시 개설하는 설계는 매우 적절하며, 이를 위해 백엔드 API를 호출하는 로직을 handleSubmit 함수에 추가

# 매우중요(******)
stomphandler 하고 jwttokenprovider 에 key를 만드는 방식은 똑같이 해야
JWT signature does not match... 에러가 안생긴다 

생긴 이유: 토큰을 생성할 때 사용한 키와 검증할 때 사용한 키의 바이트 값이 달랐음.

해결 방안 :키 문자열을 바이트로 변환할 때, 양쪽 모두 **Decoders.BASE64.decode(secretKey)**를 사용하여 키 처리 방식 일치.

# 인텔리제이 업그레이드
https://gustjr7532.tistory.com/80