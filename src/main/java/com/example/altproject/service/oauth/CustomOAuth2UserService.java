package com.example.altproject.service.oauth;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // 이거는 successhandler 까지 만들어야 해서 사용안할 가능성 높음

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId();
        String email;
        String nickname;
        String username;
        String userIdKey;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");
            username = "kakao_" + oAuth2User.getAttribute("id");
            userIdKey = "id";

        } else if ("google".equals(registrationId)) {
            email = oAuth2User.getAttribute("email");
            nickname = oAuth2User.getAttribute("name");
            username = "google_" + oAuth2User.getAttribute("sub");
            userIdKey = "sub";

        } else {
            throw new ApiException(ErrorStatus.UNSUPPORTED_OAUTH_PROVIDER,"지원하지 않는 OAuth2 Provider: " + registrationId);
        }

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(Member.createSocialMember(email, nickname,username)));

        List<String> roles = member.getMemberRoleList().stream()
                .map(Enum::name)
                .toList();

        List<GrantedAuthority> authorities = member.getMemberRoleList().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());


        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                userIdKey
        );
    }
}