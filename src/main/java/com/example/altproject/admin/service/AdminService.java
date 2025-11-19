package com.example.altproject.admin.service;

import com.example.altproject.chat.repository.ChatParticipantRepository;
import com.example.altproject.domain.board.BoardReport;
import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.member.Member;
import com.example.altproject.dto.response.BoardReportResponse;
import com.example.altproject.dto.response.MemberResponseDto;
import com.example.altproject.repository.BoardReportRepository;
import com.example.altproject.repository.BoardRepository;
import com.example.altproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final BoardReportRepository boardReportRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ChatParticipantRepository chatParticipantRepository;


    /**
     * 모든 게시글 신고 내역을 조회하여 DTO 리스트로 반환합니다.
     * @return BoardReportResponse DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<BoardReportResponse> getAllBoardReports() {
        // 1. 데이터베이스에서 모든 신고 내역을 조회합니다.
        List<BoardReport> reports = boardReportRepository.findAll();

        // 2. 조회된 엔티티 리스트를 스트림을 사용하여 DTO 리스트로 변환합니다.
        return reports.stream()
                .map(BoardReportResponse::from) // 정적 팩토리 메소드 호출
                .collect(Collectors.toList());
    }

    /**
     * 모든 회원 정보를 조회하여 DTO 리스트로 반환합니다.
     * @return MemberResponseDto DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(member -> {
                    long boardCount = boardRepository.countByWriterEmail(member.getEmail());
                    long chatRoomCount = chatParticipantRepository.countByMember(member);
                    return MemberResponseDto.from(member, boardCount, chatRoomCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 ID를 받아 계정을 정지 상태로 변경합니다.
     * @param memberId 정지할 사용자의 ID
     */
    @Transactional
    public void suspendMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_EXISTED_USER, "해당 사용자를 찾을 수 없습니다. id=" + memberId));
        member.suspend();
    }

    /**
     * 사용자의 ID를 받아 계정을 활성 상태로 변경합니다.
     * @param memberId 활성화할 사용자의 ID
     */
    @Transactional
    public void activateMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_EXISTED_USER, "해당 사용자를 찾을 수 없습니다. id=" + memberId));
        member.activate();
    }
}
