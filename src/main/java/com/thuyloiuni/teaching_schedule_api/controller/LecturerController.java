package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;
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
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
@Tag(name = "Lecturer", description = "Các API để quản lý thông tin giảng viên")
public class LecturerController {

    private final LecturerService lecturerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo giảng viên mới", description = "Tạo một tài khoản giảng viên mới. Endpoint này chỉ dành cho ADMIN.")
    public ResponseEntity<LecturerDTO> createLecturer(@Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO createdLecturer = lecturerService.createLecturer(lecturerRequestDTO);
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy tất cả giảng viên", description = "Truy xuất danh sách tất cả giảng viên. Yêu cầu đã xác thực.")
    public ResponseEntity<List<LecturerDTO>> getAllLecturers() {
        List<LecturerDTO> lecturers = lecturerService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy giảng viên theo ID", description = "Truy xuất thông tin của một giảng viên bằng ID. Yêu cầu đã xác thực.")
    public ResponseEntity<LecturerDTO> getLecturerById(@PathVariable Integer id) {
        LecturerDTO lecturer = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(lecturer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin giảng viên", description = "Cập nhật thông tin của một giảng viên. Chỉ ADMIN có quyền.")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable Integer id, @Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO updatedLecturer = lecturerService.updateLecturer(id, lecturerRequestDTO);
        return ResponseEntity.ok(updatedLecturer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa giảng viên", description = "Xóa một giảng viên. Chỉ ADMIN có quyền.")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Integer id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }
}
