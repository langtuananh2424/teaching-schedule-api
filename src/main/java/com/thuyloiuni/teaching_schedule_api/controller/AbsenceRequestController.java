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
@Tag(name = "Absence Request", description = "Endpoints for managing absence requests")
public class AbsenceRequestController {

    private final AbsenceRequestService absenceRequestService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all absence requests", description = "Retrieves a list of all absence requests. Accessible only by ADMIN.")
    public ResponseEntity<List<AbsenceRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(absenceRequestService.getAllRequests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Get absence request by ID", description = "Retrieves a single absence request by its ID.")
    public ResponseEntity<AbsenceRequestDTO> getRequestById(@PathVariable("id") Integer requestId) {
        return ResponseEntity.ok(absenceRequestService.getRequestById(requestId));
    }

    @PostMapping
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(summary = "Create a new absence request", description = "Allows a LECTURER to submit a new absence request. A makeup session can be proposed within the same request.")
    public ResponseEntity<AbsenceRequestDTO> createRequest(@Valid @RequestBody CreateAbsenceRequestDTO createDto) {
        AbsenceRequestDTO createdRequest = absenceRequestService.createRequest(createDto);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/department-approval")
    @PreAuthorize("hasRole('ADMIN')") // Assuming ADMIN can act as Department Head
    @Operation(summary = "Update department approval status", description = "Sets the approval status (APPROVED or REJECTED) for the Department level.")
    public ResponseEntity<AbsenceRequestDTO> updateDepartmentApproval(
            @PathVariable("id") Integer requestId,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        AbsenceRequestDTO updatedRequest = absenceRequestService.updateDepartmentApproval(requestId, statusDto);
        return ResponseEntity.ok(updatedRequest);
    }

    @PatchMapping("/{id}/ctsv-approval")
    @PreAuthorize("hasRole('ADMIN')") // Assuming ADMIN can act as CTSV
    @Operation(summary = "Update CTSV approval status", description = "Sets the approval status (APPROVED or REJECTED) for the CTSV (Student Affairs) level.")
    public ResponseEntity<AbsenceRequestDTO> updateCtsvApproval(
            @PathVariable("id") Integer requestId,
            @Valid @RequestBody UpdateApprovalStatusDTO statusDto) {
        AbsenceRequestDTO updatedRequest = absenceRequestService.updateCtsvApproval(requestId, statusDto);
        return ResponseEntity.ok(updatedRequest);
    }
}
