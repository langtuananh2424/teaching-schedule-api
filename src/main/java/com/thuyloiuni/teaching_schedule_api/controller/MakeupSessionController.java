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
@Tag(name = "Makeup Session", description = "Các API để quản lý các yêu cầu dạy bù")
public class MakeupSessionController {

    private final MakeupSessionService makeupSessionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả các yêu cầu dạy bù", description = "Truy xuất danh sách tất cả các yêu cầu dạy bù. Chỉ có ADMIN mới có quyền truy cập.")
    public ResponseEntity<List<MakeupSessionDTO>> getAllMakeupSessions() {
        return ResponseEntity.ok(makeupSessionService.getAllMakeupSessions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Lấy yêu cầu dạy bù theo ID", description = "Truy xuất một yêu cầu dạy bù cụ thể bằng ID của nó.")
    public ResponseEntity<MakeupSessionDTO> getMakeupSessionById(@PathVariable Integer id) {
        return ResponseEntity.ok(makeupSessionService.getMakeupSessionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Tạo một yêu cầu dạy bù mới", description = "Cho phép ADMIN hoặc LECTURER tạo một yêu cầu dạy bù mới.")
    public ResponseEntity<MakeupSessionDTO> createMakeupSession(@Valid @RequestBody CreateMakeupSessionDTO createDto) {
        MakeupSessionDTO createdSession = makeupSessionService.createMakeupSession(createDto);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/department-approval")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Bộ môn", description = "Thiết lập trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Bộ môn. Nếu cả hai cấp đều duyệt, một lịch học mới sẽ được tự động tạo cho buổi dạy bù.")
    public ResponseEntity<MakeupSessionDTO> updateDepartmentApproval(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        MakeupSessionDTO updatedSession = makeupSessionService.updateDepartmentApproval(id, statusDto);
        return ResponseEntity.ok(updatedSession);
    }

    @PatchMapping("/{id}/ctsv-approval")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Phòng CTSV", description = "Thiết lập trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Phòng CTSV. Nếu cả hai cấp đều duyệt, một lịch học mới sẽ được tự động tạo cho buổi dạy bù.")
    public ResponseEntity<MakeupSessionDTO> updateCtsvApproval(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        MakeupSessionDTO updatedSession = makeupSessionService.updateCtsvApproval(id, statusDto);
        return ResponseEntity.ok(updatedSession);
    }
}
