package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ApproveAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.service.AbsenceRequestService;
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
public class AbsenceRequestController {

    private final AbsenceRequestService absenceRequestService;

    /**
     * Lấy tất cả các đơn xin nghỉ, có thể lọc theo trạng thái.
     * Chỉ ADMIN có quyền xem.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AbsenceRequestDTO>> getAllRequests(
            @RequestParam(required = false) ApprovalStatus status) {
        List<AbsenceRequestDTO> requests;
        if (status != null) {
            requests = absenceRequestService.getRequestsByStatus(status);
        } else {
            requests = absenceRequestService.getAllRequests();
        }
        return ResponseEntity.ok(requests);
    }

    /**
     * Lấy một đơn xin nghỉ cụ thể theo ID.
     * Cả ADMIN và LECTURER đều có thể xem.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<AbsenceRequestDTO> getRequestById(@PathVariable("id") Integer requestId) {
        return ResponseEntity.ok(absenceRequestService.getRequestById(requestId));
    }

    /**
     * Giảng viên tạo một đơn xin nghỉ mới.
     */
    @PostMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<AbsenceRequestDTO> createRequest(@Valid @RequestBody CreateAbsenceRequestDTO createDto) {
        AbsenceRequestDTO createdRequest = absenceRequestService.createRequest(createDto);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    /**
     * ADMIN duyệt một đơn xin nghỉ.
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbsenceRequestDTO> approveRequest(
            @PathVariable("id") Integer requestId,
            @Valid @RequestBody ApproveAbsenceRequestDTO approveDto) {
        AbsenceRequestDTO updatedRequest = absenceRequestService.approveRequest(requestId, approveDto);
        return ResponseEntity.ok(updatedRequest);
    }
}
