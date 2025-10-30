package com.example.altproject.controller;

import com.example.altproject.common.api.ApiResponse;
import com.example.altproject.dto.request.BoardReportRequest;
import com.example.altproject.dto.request.BoardRequest;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;


    // 게시글 작성
    @PostMapping("/create")
    public ApiResponse<BoardResponse> createBoard(@RequestBody BoardRequest dto, @AuthenticationPrincipal Object principal) {

        BoardResponse response = boardService.createBoard(dto,principal);
        return ApiResponse.Success(response);
    }

    // 게시글 수정
    @PatchMapping("/update/{boardId}")
    public ApiResponse<BoardResponse> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequest dto, @AuthenticationPrincipal Object principal) {
        System.out.println("request = "+ dto);
        BoardResponse response = boardService.patchBoard(boardId, dto,principal);
        return ApiResponse.Success(response);
    }

    // 특정 게시물
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

    // 전체 게시물 리스트
    @GetMapping("/latest-list")
    public ApiResponse<List<BoardResponse>> getLatestBoardList() {
        List<BoardResponse> response = boardService.getLatestBoardList();
        return ApiResponse.Success(response);
    }

    // 게시물 상세보기
    @GetMapping("/detail/{boardId}")
    public ApiResponse<BoardResponse> BoardDetail(@PathVariable Long boardId){
        BoardResponse response = boardService.boardDetail(boardId);
        return ApiResponse.Success(response);
    }

    //자신이 작성한 게시물 리스트
    @GetMapping("/user-board-list/{email}")
    public ApiResponse<List<BoardResponse>>  getMyBoardList(@PathVariable("email") String email) {
        List<BoardResponse> response = boardService.getUserBoardList(email);
        System.out.println("response= "+response);
        return ApiResponse.Success(response);
    }


    // 내가 참여한 게시물 리스트
    @GetMapping("/participated-boards")
    public ApiResponse<List<BoardResponse>> getMychatBoard(@AuthenticationPrincipal Object principal){
        List<BoardResponse> response = boardService.getUserBoardParticipation(principal);
        return ApiResponse.Success(response);
    }


    // 신고
    @PostMapping("/{boardId}/report")
    public ResponseEntity<String> reportBoard(
            @PathVariable Long boardId,
            @RequestBody BoardReportRequest request,
            @AuthenticationPrincipal Object principal) {
        boardService.reportBoard(boardId, principal, request);
        return ResponseEntity.ok("게시글이 정상적으로 신고되었습니다.");
    }

    // view count
    @PatchMapping("/{boardId}/view")
    public ResponseEntity<Void> increaseViewCount(@PathVariable Long boardId) {
        boardService.increaseViewCount(boardId);
        return ResponseEntity.ok().build();
    }


//    //좋아요
//    @PutMapping("/{boardNumber}/favorite")
//    public ApiResponse<? super PutFavoriteResponseDto> putFavorite(
//            @PathVariable("boardNumber") Integer boardNumber,
//            @AuthenticationPrincipal String email
//    ){
//        ResponseEntity<? super PutFavoriteResponseDto> response = boardService.putFavorite(boardNumber, email);
//        return response;
//    }
//
//    //좋아요 게시물
//    @GetMapping("/{boardNumber}/favorite-list")
//    public ResponseEntity<? super GetFavoriteListResponseDto> getFavoriteList(@PathVariable("boardNumber") Integer boardNumber) {
//        ResponseEntity<? super GetFavoriteListResponseDto> response = boardService.getFavoriteList(boardNumber);
//        return response;
//    }
//
//    //카운트 증가 api (이걸 만든 이유는 react에서 상세 페이지를 한버만 들어갔는데 뷰카운트가 4씩 증가 하는 현상을 막기 위해 구현)
//    @GetMapping("/{boardNumber}/increase-view-count")
//    public ResponseEntity<? super IncreaseViewCountResponseDto> increaseViewCount(@PathVariable("boardNumber") Integer boardNumber) {
//        ResponseEntity<? super IncreaseViewCountResponseDto> response = boardService.increaseViewCount(boardNumber);
//        return response;
//    }
//

//    //top3 리스트
//    @GetMapping("/top-3")
//    public ResponseEntity<? super GetTop3BoardListResponseDto> getTop3BoardList() {
//        ResponseEntity<? super GetTop3BoardListResponseDto> response = boardService.getTop3BoardList();
//        return response;
//    }
//
//    //검색 게시물 리스트
//    @GetMapping(value = {"/search-list/{searchWord}","/search-list/{searchWord}/{preSearchWord}"})
//    public ResponseEntity<? super GetSearchBoardListResponseDto> getSearchBoardList(
//            @PathVariable("searchWord") String keyword,
//            @PathVariable(value = "preSearchWord",required = false) String preSearchWord) {
//        ResponseEntity<? super GetSearchBoardListResponseDto> response = boardService.getSearchBoardList(keyword,preSearchWord);
//        return response;
//    }



}
