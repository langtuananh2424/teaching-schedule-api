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
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy tất cả đơn xin nghỉ", description = "Truy xuất danh sách tất cả các đơn xin nghỉ. ADMIN có thể xem tất cả, MANAGER chỉ xem được các yêu cầu của khoa mình.")
    public ResponseEntity<List<AbsenceRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(absenceRequestService.getAllRequests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'MANAGER')")
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

    @PatchMapping("/{id}/manager-approval")
    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Trưởng khoa (Manager)", description = "Thiết lập trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Trưởng khoa (Manager). Chỉ Manager mới có quyền này.")
    public ResponseEntity<AbsenceRequestDTO> updateManagerApproval(
            @PathVariable("id") Integer requestId,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        AbsenceRequestDTO updatedRequest = absenceRequestService.updateManagerApproval(requestId, statusDto);
        return ResponseEntity.ok(updatedRequest);
    }

    @PatchMapping("/{id}/academic-affairs-approval")
    @PreAuthorize("hasRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cập nhật trạng thái duyệt của Phòng Đào tạo", description = "Thiết lập trạng thái duyệt (Đã duyệt hoặc Đã từ chối) cho cấp Phòng Đào tạo.")
    public ResponseEntity<AbsenceRequestDTO> updateAcademicAffairsApproval(
            @PathVariable("id") Integer requestId,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        AbsenceRequestDTO updatedRequest = absenceRequestService.updateAcademicAffairsApproval(requestId, statusDto);
        return ResponseEntity.ok(updatedRequest);
    }
}
