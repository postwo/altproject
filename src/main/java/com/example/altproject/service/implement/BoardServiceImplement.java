package com.example.altproject.service.implement;

import com.example.altproject.chat.domain.ChatParticipant;
import com.example.altproject.chat.domain.ChatRoom;
import com.example.altproject.chat.repository.ChatParticipantRepository;
import com.example.altproject.chat.repository.ChatRoomRepository;
import com.example.altproject.common.AuthUtil;
import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.board.BoardReport;
import com.example.altproject.domain.favorite.Favorite;
import com.example.altproject.domain.hashtag.HashTag;
import com.example.altproject.domain.image.Image;
import com.example.altproject.domain.member.Member;
import com.example.altproject.dto.request.BoardReportRequest;
import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.repository.*;
import com.example.altproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImplement implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final HashTagRepository hashTagRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final BoardReportRepository boardReportRepository;
    private final FavoriteRepository favoriteRepository;
    private final AuthUtil authUtil;


    @Override
    @Transactional
    public BoardResponse createBoard(BoardRequest request, Object principal) {

        String email = authUtil.getEmail(principal);

        Member author = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. Email: " + email));

        System.out.println("request parti" + request.getMaxParticipants());

        Board board = Board.create(request,author);

        if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
            Set<HashTag> hashtags = request.getHashtags().stream()
                    .map(name -> hashTagRepository.findByHashtagName(name)
                            .orElseGet(() -> hashTagRepository.save(
                                    HashTag.builder().hashtagName(name).build()
                            )))
                    .collect(Collectors.toSet());
            board.getHashtags().addAll(hashtags);
        }

        if (request.getBoardImageList() != null && !request.getBoardImageList().isEmpty()) {
            List<Image> imageEntities = request.getBoardImageList().stream()
                    .map(imageUrl -> new Image(board, imageUrl))
                    .collect(Collectors.toList());

            board.getImages().addAll(imageEntities);
        }

        boardRepository.save(board);

        return BoardResponse.createResponse(board);
    }

    @Override
    @Transactional
    public BoardResponse patchBoard(Long boardId, BoardRequest request, Object principal) {

        String email = authUtil.getEmail(principal);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD, "해당 게시글이 존재하지 않습니다. "));

        // 이 검증을 통과하면 'email' 변수는 게시글 작성자의 이메일임이 보장됩니다.
        validateWriter(board, email);

        String oldTitle = board.getTitle();

        // 채팅방을 이름으로 찾되, 만약 존재하지 않으면 새로 생성하여 대처합니다.
        ChatRoom chatRoom = chatRoomRepository.findByName(oldTitle)
                .orElseGet(() -> {
                    System.out.println("기존 채팅방이 없어 새로 생성합니다. Title: " + oldTitle);

                    // 1. 이 게시글을 위한 새로운 채팅방을 생성합니다.
                    ChatRoom newChatRoom = ChatRoom.builder()
                            .name(oldTitle)
                            .build();

                    // ✨ 해결: 'email' 변수를 사용해 DB에서 Member 객체를 직접 조회합니다.
                    Member author = memberRepository.findByEmail(email)
                            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER, "작성자 정보를 찾을 수 없습니다."));

                    // 2. 조회한 작성자(author) 객체로 참여자를 생성합니다.
                    ChatParticipant participant = ChatParticipant.builder()
                            .member(author)
                            .chatRoom(newChatRoom)
                            .build();
                    chatParticipantRepository.save(participant);

                    // 3. 완성된 새 채팅방을 DB에 저장하고 반환합니다.
                    return chatRoomRepository.save(newChatRoom);
                });

        // 이제 안심하고 게시글 정보를 업데이트합니다.
        board.update(request);

        // 위 로직을 통해 '찾아오거나' 또는 '새로 생성한' chatRoom 객체의 이름을 "새로운 제목"으로 변경합니다.
        chatRoom.setName(board.getTitle());

        // --- 이하 해시태그, 이미지 업데이트 로직은 동일 ---
        if (request.getHashtags() != null) {
            Set<HashTag> newHashtags = request.getHashtags().stream()
                    .map(name -> hashTagRepository.findByHashtagName(name)
                            .orElseGet(() -> hashTagRepository.save(
                                    HashTag.builder().hashtagName(name).build()
                            )))
                    .collect(Collectors.toSet());

            board.getHashtags().clear();
            board.getHashtags().addAll(newHashtags);
        }

        if (request.getBoardImageList() != null) {
            List<Image> imageEntities = request.getBoardImageList().stream()
                    .map(imageUrl -> new Image(board, imageUrl))
                    .collect(Collectors.toList());

            board.getImages().clear();
            board.getImages().addAll(imageEntities);
        }

        return BoardResponse.updateResponse(board);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD, "해당 게시글이 존재하지 않습니다."));

        return BoardResponse.getResponse(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId, Object principal) {
        String email = authUtil.getEmail(principal);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD, "해당 게시글이 존재하지 않습니다."));

        validateWriter(board, email);

        boardRepository.delete(board);

        chatRoomRepository.deleteByName(board.getTitle());
    }


    private void validateWriter(Board board, String email) {
        if (!board.getWriterEmail().equals(email)) {
            throw new ApiException(ErrorStatus.NO_PERMISSION, "게시글을 수정,삭제할 권한이 없습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getLatestBoardList() {
        List<Board> latestBoards = boardRepository.findAllWithDetails();

        List<BoardResponse> responseList = latestBoards.stream()
                .map(BoardResponse::getResponse)
                .collect(Collectors.toList());
        return responseList;
    }

    // 게시글 상세보기
    @Override
    @Transactional // 조회수 증가(쓰기 작업)가 필요하므로 @Transactional을 유지합니다.
    public BoardResponse boardDetail(Long boardId) {
        // 1. 게시글 조회
        // Lazy Loading으로 인한 N+1 문제를 방지하기 위해 Fetch Join을 사용하는 BoardRepository 메서드를 사용하는 것이 일반적입니다.
        // 현재 BoardRepository에 @EntityGraph가 정의된 findAllWithDetails()만 있으므로,
        // findById()를 사용하되 N+1 문제를 감안하거나 (임시 방편), 상세 조회용 Fetch Join 쿼리를 추가해야 합니다.

        Board board = boardRepository.findByIdWithDetails(boardId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD, "해당 게시글이 존재하지 않습니다. Board ID: " + boardId));

        ChatRoom chatRoom = chatRoomRepository.findByName(board.getTitle()).orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_CHATROOM,"해당 제목에 채팅방이 없습니다"));

        return BoardResponse.getResponseChat(board,chatRoom);
    }

    @Transactional
    public void increaseViewCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD));
        board.increaseViewCount();
    }

    @Override
    @Transactional
    public void putFavorite(Long boardId, Object principal) {
        String email = authUtil.getEmail(principal);
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new ApiException(ErrorStatus.NOT_EXISTED_USER,"해당 사용자가 존재하지 않습니다."));

        Board board = boardRepository.findById(boardId).orElseThrow(()->new ApiException(ErrorStatus.NOT_EXISTED_BOARD,"해당 게시글이 존재하지 않습니다."));

        Favorite favorite = favoriteRepository.findByBoardIdAndMemberEmail(boardId,email);

        if (favorite == null) {
            favorite = new Favorite(member,board);
            favoriteRepository.save(favorite);
            board.increaseFavoriteCount();
        }
        else {
            favoriteRepository.delete(favorite);
            board.decreaseFavoriteCount();
        }

        boardRepository.save(board);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getUserBoardList(String email) {

        // 1. 이메일로 사용자를 조회합니다. 없으면 예외를 발생시킵니다.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER,"해당 사용자를 찾을 수 없습니다. email=" + email));

        // 2. 해당 사용자가 작성한 모든 게시글을 최신순으로 조회합니다.
        List<Board> boards = boardRepository.findByWriterEmailOrderByCreatedAtDesc(member.getEmail());

        if (boards.isEmpty()) {
            throw new ApiException(ErrorStatus.NOT_EXISTED_BOARD,"작성한 게시글이 없습니다.");
        }

        return boards.stream()
                .map(BoardResponse::userBoardList)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getUserBoardParticipation(Object principal) {
        // 1. principal 객체로부터 현재 로그인한 사용자의 이메일을 가져옵니다.
        String email = authUtil.getEmail(principal);

        // 2. 이메일을 통해 'Member' 엔티티를 먼저 조회합니다.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER, "해당 사용자를 찾을 수 없습니다."));

        // 3. 사용자가 참여한 채팅방 목록 조회 (ChatParticipant를 통해)
        List<ChatParticipant> participants = chatParticipantRepository.findByMember(member);
        
        if (participants.isEmpty()) {
            throw new ApiException(ErrorStatus.NOT_EXISTED_CHATROOM, "참여한 채팅방이 없습니다");
        }

        // 4. 채팅방 이름 리스트 추출
        List<String> roomNames = participants.stream()
                .map(participant -> participant.getChatRoom().getName())
                .collect(Collectors.toList());

        // 5. 채팅방 이름과 일치하는 제목의 게시글 조회
        List<Board> boards = boardRepository.findByTitleIn(roomNames);

        // 6. Board 엔티티를 BoardResponse DTO로 변환하여 반환
        return boards.stream()
                .map(BoardResponse::userBoardList)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reportBoard(Long boardId, Object principal, BoardReportRequest request) {

        String email = authUtil.getEmail(principal);
        // 1. 신고자(Member)와 신고된 게시글(Board)을 조회합니다.
        Member reporter = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_USER));

        Board reportedBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD));

        // 2. [핵심] 사용자가 동일한 게시글을 이미 신고했는지 확인합니다.
        if (boardReportRepository.existsByReporterAndReportedBoard(reporter, reportedBoard)) {
            throw new ApiException(ErrorStatus.ALREADY_REPORTED_BOARD);
        }

        // 3. 신고 내역(BoardReport) 엔티티를 생성합니다.
        BoardReport report = BoardReport.builder()
                .reporter(reporter)
                .reportedBoard(reportedBoard)
                .reason(request.getReason())
                .details(request.getDetails())
                .build();

        // 4. 생성된 신고 내역을 데이터베이스에 저장합니다.
        boardReportRepository.save(report);
    }

}
