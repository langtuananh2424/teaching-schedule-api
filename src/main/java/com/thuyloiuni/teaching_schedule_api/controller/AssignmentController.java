package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.AssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.service.AssignmentService;
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
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignment", description = "Các API để quản lý phân công giảng dạy")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả các phân công", description = "Truy xuất danh sách tất cả các phân công giảng dạy. Chỉ ADMIN có quyền.")
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Lấy phân công theo ID", description = "Truy xuất thông tin một phân công giảng dạy cụ thể bằng ID.")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    @GetMapping("/by-lecturer/{lecturerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Lấy phân công theo giảng viên", description = "Truy xuất danh sách các phân công giảng dạy của một giảng viên cụ thể.")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByLecturer(@PathVariable Integer lecturerId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByLecturer(lecturerId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo phân công mới", description = "Tạo một phân công giảng dạy mới. Chỉ ADMIN có quyền.")
    public ResponseEntity<AssignmentDTO> createAssignment(@Valid @RequestBody CreateAssignmentDTO createDto) {
        AssignmentDTO createdAssignment = assignmentService.createAssignment(createDto);
        return new ResponseEntity<>(createdAssignment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật phân công", description = "Cập nhật thông tin một phân công giảng dạy đã có. Chỉ ADMIN có quyền.")
    public ResponseEntity<AssignmentDTO> updateAssignment(@PathVariable Integer id, @Valid @RequestBody CreateAssignmentDTO updateDto) {
        AssignmentDTO updatedAssignment = assignmentService.updateAssignment(id, updateDto);
        return ResponseEntity.ok(updatedAssignment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa phân công", description = "Xóa một phân công giảng dạy khỏi hệ thống. Chỉ ADMIN có quyền.")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Integer id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
