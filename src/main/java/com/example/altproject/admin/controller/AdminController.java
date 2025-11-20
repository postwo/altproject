package com.example.altproject.admin.controller;

import com.example.altproject.admin.service.AdminService;
import com.example.altproject.dto.response.BoardResponse;
import com.example.altproject.dto.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }

    // 게시글 목록
    @GetMapping("/boards")
    public ResponseEntity<List<BoardResponse>> getAllBoards() {
        return ResponseEntity.ok(adminService.getAllBoards());
    }

    @PatchMapping("/members/{id}/suspend")
    public ResponseEntity<Void> suspendMember(@PathVariable Long id) {
        adminService.suspendMember(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/members/{id}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable Long id) {
        adminService.activateMember(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 삭제
     * @param boardId 삭제할 게시글 ID
     * @return 200 OK (성공 시)
     */
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        adminService.deleteBoard(boardId);
        return ResponseEntity.ok().build();
    }

    /**
     * 전체 회원 수 조회
     * @return 전체 회원 수 (JSON 형태: {"totalMembers": 123})
     */
    @GetMapping("/members/count")
    public ResponseEntity<Map<String, Long>> getTotalMemberCount() {
        log.info("들어왔어 ");
        long count = adminService.getTotalMemberCount();
        log.info("숫자"+count);
        return ResponseEntity.ok(Collections.singletonMap("totalMembers", count));
    }

    // getReportCount() 메서드는 현재 게시글 목록과 관련이 없으므로 수정하지 않습니다.
    // 필요하다면 별도로 요청해주세요.
    // @GetMapping("/report/count")
    // public ResponseEntity<> getReportCount() {
    //
    // }
}
