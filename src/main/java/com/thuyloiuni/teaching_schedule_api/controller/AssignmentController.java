package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.AssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    /**
     * Lấy tất cả các phân công. ADMIN có thể xem tất cả.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    /**
     * Lấy một phân công cụ thể theo ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    /**
     * Lấy tất cả phân công của một giảng viên.
     */
    @GetMapping("/by-lecturer/{lecturerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByLecturer(@PathVariable Integer lecturerId) {
        // Cần thêm logic kiểm tra xem giảng viên đang đăng nhập có phải là lecturerId không, hoặc là ADMIN.
        return ResponseEntity.ok(assignmentService.getAssignmentsByLecturer(lecturerId));
    }

    /**
     * Tạo một phân công mới. Chỉ ADMIN có quyền.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignmentDTO> createAssignment(@Valid @RequestBody CreateAssignmentDTO createDto) {
        AssignmentDTO createdAssignment = assignmentService.createAssignment(createDto);
        return new ResponseEntity<>(createdAssignment, HttpStatus.CREATED);
    }

    /**
     * Cập nhật một phân công. Chỉ ADMIN có quyền.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignmentDTO> updateAssignment(@PathVariable Integer id, @Valid @RequestBody CreateAssignmentDTO updateDto) {
        AssignmentDTO updatedAssignment = assignmentService.updateAssignment(id, updateDto);
        return ResponseEntity.ok(updatedAssignment);
    }

    /**
     * Xóa một phân công. Chỉ ADMIN có quyền.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Integer id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
