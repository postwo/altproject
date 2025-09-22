package com.example.altproject.service.implement;

import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.hashtag.HashTag;
import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.repository.BoardRepository;
import com.example.altproject.repository.HashTagRepository;
import com.example.altproject.repository.MemberRepository;
import com.example.altproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImplement implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final HashTagRepository hashTagRepository;

    @Override
    @Transactional
    public BoardResponse createBoard(BoardRequest request) {
        Board board = Board.create(request);

        if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
            Set<HashTag> hashtags = request.getHashtags().stream()
                    .map(name -> hashTagRepository.findByHashtagName(name)
                            .orElseGet(() -> hashTagRepository.save(
                                    HashTag.builder().hashtagName(name).build()
                            )))
                    .collect(Collectors.toSet());
            board.getHashtags().addAll(hashtags);
        }

        // 저장
        boardRepository.save(board);

        // 응답 DTO 변환
        return BoardResponse.createResponse(board);
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardRequest request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + boardId));

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

        boardRepository.save(board);

        return BoardResponse.updateResponse(board);
    }
}
