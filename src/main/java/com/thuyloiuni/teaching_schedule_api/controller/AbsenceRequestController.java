package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;
import com.thuyloiuni.teaching_schedule_api.service.AbsenceRequestService;
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
@RequestMapping("/api/absence-requests")
@RequiredArgsConstructor
@Tag(name = "Absence Request", description = "Các API để quản lý yêu cầu xin nghỉ của giảng viên")
public class AbsenceRequestController {

    private final AbsenceRequestService absenceRequestService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả đơn xin nghỉ", description = "Truy xuất danh sách tất cả các đơn xin nghỉ. Chỉ có ADMIN mới có quyền truy cập.")
    public ResponseEntity<List<AbsenceRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(absenceRequestService.getAllRequests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Lấy đơn xin nghỉ theo ID", description = "Truy xuất một đơn xin nghỉ cụ thể bằng ID của nó.")
    public ResponseEntity<AbsenceRequestDTO> getRequestById(@PathVariable("id") Integer requestId) {
        return ResponseEntity.ok(absenceRequestService.getRequestById(requestId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('LECTURER') and #createDto.lecturerId == principal.lecturerId)")
    @Operation(summary = "Tạo một đơn xin nghỉ mới", description = "Cho phép ADMIN tạo đơn xin nghỉ cho bất kỳ giảng viên nào, hoặc LECTURER tự tạo đơn xin nghỉ cho chính mình.")
    public ResponseEntity<AbsenceRequestDTO> createRequest(@Valid @RequestBody CreateAbsenceRequestDTO createDto) {
        AbsenceRequestDTO createdRequest = absenceRequestService.createRequest(createDto);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/department-approval")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Bộ môn", description = "Thiết lập trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Bộ môn.")
    public ResponseEntity<AbsenceRequestDTO> updateDepartmentApproval(
            @PathVariable("id") Integer requestId,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        AbsenceRequestDTO updatedRequest = absenceRequestService.updateDepartmentApproval(requestId, statusDto);
        return ResponseEntity.ok(updatedRequest);
    }

    @PatchMapping("/{id}/ctsv-approval")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Phòng CTSV", description = "Thiết lập trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Phòng Công tác Sinh viên.")
    public ResponseEntity<AbsenceRequestDTO> updateCtsvApproval(
            @PathVariable("id") Integer requestId,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        AbsenceRequestDTO updatedRequest = absenceRequestService.updateCtsvApproval(requestId, statusDto);
        return ResponseEntity.ok(updatedRequest);
    }
}
