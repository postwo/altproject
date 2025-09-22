package com.example.altproject.service;

import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;

public interface BoardService {

    BoardResponse createBoard(BoardRequest request);
    BoardResponse updateBoard(Long boardId,BoardRequest request);

}
