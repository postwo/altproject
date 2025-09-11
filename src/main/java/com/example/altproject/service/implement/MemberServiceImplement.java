package com.example.altproject.service.implement;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.dto.request.SignUpRequest;
import com.example.altproject.dto.response.SignUpResponse;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImplement implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(ErrorStatus.DUPLICATE_EMAIL);
        }

        if (memberRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new ApiException(ErrorStatus.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.createMember(request.getEmail(), encodedPassword, request.getNickname());
        Member savedMember = memberRepository.save(member);

        SignUpResponse response = SignUpResponse.responseMember(savedMember);
        return response;
    }
}

