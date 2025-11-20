package com.example.altproject.admin.service;

import com.example.altproject.chat.repository.ChatParticipantRepository;
import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.board.BoardReport;
import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.hashtag.HashTag;
import com.example.altproject.domain.member.Member;
import com.example.altproject.dto.response.BoardReportResponse;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.dto.response.MemberResponseDto;
import com.example.altproject.repository.BoardReportRepository;
import com.example.altproject.repository.BoardRepository;
import com.example.altproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * 모든 게시글 정보를 신고 횟수, 해시태그와 함께 조회하여 DTO 리스트로 반환합니다.
     * @return BoardResponse DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<BoardResponse> getAllBoards() {
        // 1. JPQL을 통해 기본 정보와 신고 횟수를 조회
        List<BoardResponse> responses = boardRepository.findAllWithReportCount();

        if (responses.isEmpty()) {
            return responses;
        }

        // 2. 조회된 게시글들의 ID를 리스트로 추출
        List<Long> boardIds = responses.stream().map(BoardResponse::getId).collect(Collectors.toList());

        // 3. ID 리스트를 사용하여 모든 관련 게시글과 해시태그를 한 번의 쿼리로 가져옴 (Fetch Join)
        List<Board> boardsWithHashtags = boardRepository.findAllByIdInWithHashtags(boardIds);

        // 4. 게시글 ID를 키로 하는 해시태그 Set 맵을 생성
        Map<Long, Set<String>> hashtagMap = boardsWithHashtags.stream()
                .collect(Collectors.toMap(
                        Board::getId,
                        board -> board.getHashtags().stream()
                                .map(HashTag::getHashtagName)
                                .collect(Collectors.toSet())
                ));

        // 5. 기존 DTO 리스트에 해시태그 정보를 채워줌
        responses.forEach(response -> response.setHashtags(hashtagMap.get(response.getId())));
        return responses;
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

    /**
     * 게시글을 삭제합니다.
     * @param boardId 삭제할 게시글 ID
     */
    @Transactional
    public void deleteBoard(Long boardId) {
        // 게시글이 존재하는지 확인
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD, "해당 게시글을 찾을 수 없습니다. id=" + boardId));
        
        // 연관된 신고 내역 삭제 (선택사항: 필요에 따라 주석 처리 가능)
        boardReportRepository.deleteByReportedBoard(board);
        
        // 게시글 삭제
        boardRepository.delete(board);
    }

    /**
     * 전체 회원 수를 조회합니다.
     * @return 전체 회원 수
     */
    @Transactional(readOnly = true)
    public long getTotalMemberCount() {
        return memberRepository.count();
    }
}
