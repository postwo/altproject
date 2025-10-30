package com.example.altproject.service;

import com.example.altproject.dto.request.BoardReportRequest;
import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;

import java.util.List;

public interface BoardService {

    BoardResponse createBoard(BoardRequest request, Object principal);
    BoardResponse patchBoard(Long boardId,BoardRequest request,Object principal);
    BoardResponse getBoard(Long boardId);
    void deleteBoard(Long boardId, Object principal);

    List<BoardResponse> getLatestBoardList();
    BoardResponse boardDetail(Long boardId);

    List<BoardResponse> getUserBoardList(String email);

    List<BoardResponse> getUserBoardParticipation(Object principal);

    void reportBoard(Long boardId,Object principal, BoardReportRequest request);

    void increaseViewCount(Long boardId);
}

