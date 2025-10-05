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