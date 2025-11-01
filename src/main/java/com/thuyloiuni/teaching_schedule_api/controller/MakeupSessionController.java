package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;
import com.thuyloiuni.teaching_schedule_api.service.MakeupSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Makeup Session", description = "Endpoints for managing makeup teaching sessions")
public class MakeupSessionController {

    private final MakeupSessionService makeupSessionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all makeup sessions", description = "Retrieves a list of all makeup sessions. Accessible only by ADMIN.")
    public ResponseEntity<List<MakeupSessionDTO>> getAllMakeupSessions() {
        return ResponseEntity.ok(makeupSessionService.getAllMakeupSessions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Get makeup session by ID", description = "Retrieves a single makeup session by its ID.")
    public ResponseEntity<MakeupSessionDTO> getMakeupSessionById(@PathVariable Integer id) {
        return ResponseEntity.ok(makeupSessionService.getMakeupSessionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Create a new makeup session request", description = "Allows an ADMIN or LECTURER to submit a new request for a makeup session.")
    public ResponseEntity<MakeupSessionDTO> createMakeupSession(@Valid @RequestBody CreateMakeupSessionDTO createDto) {
        MakeupSessionDTO createdSession = makeupSessionService.createMakeupSession(createDto);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/department-approval")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Bộ môn", description = "Cập nhật trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Bộ môn. Nếu cả hai cấp đều duyệt, một lịch học mới sẽ được tự động tạo cho buổi dạy bù.")
    public ResponseEntity<MakeupSessionDTO> updateDepartmentApproval(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        MakeupSessionDTO updatedSession = makeupSessionService.updateDepartmentApproval(id, statusDto);
        return ResponseEntity.ok(updatedSession);
    }

    @PatchMapping("/{id}/ctsv-approval")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Phòng CTSV", description = "Cập nhật trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Phòng CTSV. Nếu cả hai cấp đều duyệt, một lịch học mới sẽ được tự động tạo cho buổi dạy bù.")
    public ResponseEntity<MakeupSessionDTO> updateCtsvApproval(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        MakeupSessionDTO updatedSession = makeupSessionService.updateCtsvApproval(id, statusDto);
        return ResponseEntity.ok(updatedSession);
    }
}
