package com.example.altproject.admin.controller;

import com.example.altproject.admin.service.AdminService;
import com.example.altproject.dto.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
