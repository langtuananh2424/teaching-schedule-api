package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.ApproveMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.service.MakeupSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/makeup-sessions")
@RequiredArgsConstructor
public class MakeupSessionController {

    private final MakeupSessionService makeupSessionService;

    /**
     * Lấy tất cả các đăng ký dạy bù, có thể lọc theo trạng thái.
     * Chỉ ADMIN có quyền xem tất cả.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MakeupSessionDTO>> getAllMakeupSessions(
            @RequestParam(required = false) ApprovalStatus status) {
        List<MakeupSessionDTO> sessions;
        if (status != null) {
            sessions = makeupSessionService.getMakeupSessionsByStatus(status);
        } else {
            sessions = makeupSessionService.getAllMakeupSessions();
        }
        return ResponseEntity.ok(sessions);
    }

    /**
     * Lấy một đăng ký dạy bù cụ thể theo ID.
     * Cả ADMIN và LECTURER đều có thể xem.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<MakeupSessionDTO> getMakeupSessionById(@PathVariable Integer id) {
        return ResponseEntity.ok(makeupSessionService.getMakeupSessionById(id));
    }

    /**
     * Giảng viên tạo một đăng ký dạy bù mới.
     */
    @PostMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<MakeupSessionDTO> createMakeupSession(@Valid @RequestBody CreateMakeupSessionDTO createDto) {
        MakeupSessionDTO createdSession = makeupSessionService.createMakeupSession(createDto);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    /**
     * ADMIN duyệt một đăng ký dạy bù.
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MakeupSessionDTO> approveMakeupSession(
            @PathVariable Integer id,
            @Valid @RequestBody ApproveMakeupSessionDTO approveDto) {
        MakeupSessionDTO updatedSession = makeupSessionService.approveMakeupSession(id, approveDto);
        return ResponseEntity.ok(updatedSession);
    }
}
