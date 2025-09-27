package com.example.altproject.controller;

import com.example.altproject.common.AuthUtil;
import com.example.altproject.common.api.ApiResponse;
import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final AuthUtil authUtil;

    @PostMapping("/create")
    public ApiResponse<BoardResponse> createBoard(@RequestBody BoardRequest dto, @AuthenticationPrincipal Object principal) {

        BoardResponse response = boardService.createBoard(dto,principal);
        return ApiResponse.Success(response);
    }

    @PatchMapping("/update/{boardId}")
    public ApiResponse<BoardResponse> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequest dto, @AuthenticationPrincipal Object principal) {
        BoardResponse response = boardService.patchBoard(boardId, dto,principal);
        return ApiResponse.Success(response);
    }

    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse> getBoard(@PathVariable Long boardId) {
        BoardResponse response = boardService.getBoard(boardId);
        return ApiResponse.Success(response);
    }

    @DeleteMapping("/delete/{boardId}")
    public ApiResponse<String> deleteBoard(@PathVariable Long boardId,
                                           @AuthenticationPrincipal Object principal) {
        boardService.deleteBoard(boardId, principal);
        return ApiResponse.Success("게시글이 성공적으로 삭제되었습니다.");
    }


}
