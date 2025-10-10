package com.example.altproject.service.implement;

import com.example.altproject.common.AuthUtil;
import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.hashtag.HashTag;
import com.example.altproject.domain.image.Image;
import com.example.altproject.domain.member.Member;
import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.repository.BoardRepository;
import com.example.altproject.repository.HashTagRepository;
import com.example.altproject.repository.MemberRepository;
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
    private final AuthUtil authUtil;


    @Override
    @Transactional
    public BoardResponse createBoard(BoardRequest request, Object principal) {

        String email = authUtil.getEmail(principal);

        Member author = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. Email: " + email));

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
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_EXISTED_BOARD,"해당 게시글이 존재하지 않습니다. "));

        validateWriter(board, email);

        board.update(request);

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

        boardRepository.save(board);

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
}
