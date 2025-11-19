package com.example.altproject.service.security;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.domain.member.status.MemberStatus;
import com.example.altproject.filter.CustomUserDetails;
import com.example.altproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.getWithRoles(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER,"사용자를 찾을 수 없습니다: " + email));
        return new CustomUserDetails(member);
    }
}
