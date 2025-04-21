package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> checkAdminAccess() {
        return ResponseEntity.ok("✅ 관리자 권한 인증 성공!");
    }
}

