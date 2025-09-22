package com.example.altproject.controller;

import com.example.altproject.common.AuthUtil;
import com.example.altproject.common.api.ApiResponse;
import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final AuthUtil authUtil;

    // @AuthenticationPrincipal Object principal
    @PostMapping("/create")
    public ApiResponse<BoardResponse> createBoard(@RequestBody BoardRequest dto) {

        // ⭐ AuthUtil을 사용하여 로그인 상태를 검증
        authUtil.validateLoggedInUser();

        BoardResponse response = boardService.createBoard(dto);
        return ApiResponse.Success(response);
    }

    @PutMapping("/update/{boardId}")
    public ApiResponse<BoardResponse> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequest dto) {
        authUtil.validateLoggedInUser();
        BoardResponse response = boardService.updateBoard(boardId, dto);
        return ApiResponse.Success(response);
    }


}
