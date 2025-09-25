package com.example.altproject.service;

import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;

public interface BoardService {

    BoardResponse createBoard(BoardRequest request, Object principal);
    BoardResponse updateBoard(Long boardId,BoardRequest request,Object principal);
}

