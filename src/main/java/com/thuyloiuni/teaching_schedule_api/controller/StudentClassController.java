package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.service.StudentClassService;
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
@RequestMapping("/api/student-classes")
@RequiredArgsConstructor
@Tag(name = "Student Class", description = "Các API để quản lý các lớp sinh viên")
public class StudentClassController {

    private final StudentClassService studentClassService;

    @GetMapping
    @Operation(summary = "Lấy tất cả các lớp sinh viên", description = "Truy xuất danh sách tất cả các lớp sinh viên trong hệ thống.")
    public ResponseEntity<List<StudentClassDTO>> getAllClasses() {
        return ResponseEntity.ok(studentClassService.getAllClasses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy lớp sinh viên theo ID", description = "Truy xuất thông tin một lớp sinh viên cụ thể bằng ID.")
    public ResponseEntity<StudentClassDTO> getClassById(@PathVariable Integer id) {
        return ResponseEntity.ok(studentClassService.getClassById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo lớp sinh viên mới", description = "Tạo một lớp sinh viên mới. Chỉ ADMIN có quyền.")
    public ResponseEntity<StudentClassDTO> createClass(@Valid @RequestBody StudentClassDTO studentClassDTO) {
        StudentClassDTO createdClass = studentClassService.createClass(studentClassDTO);
        return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật lớp sinh viên", description = "Cập nhật thông tin một lớp sinh viên đã có. Chỉ ADMIN có quyền.")
    public ResponseEntity<StudentClassDTO> updateClass(@PathVariable Integer id, @Valid @RequestBody StudentClassDTO studentClassDTO) {
        StudentClassDTO updatedClass = studentClassService.updateClass(id, studentClassDTO);
        return ResponseEntity.ok(updatedClass);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa lớp sinh viên", description = "Xóa một lớp sinh viên khỏi hệ thống. Chỉ ADMIN có quyền.")
    public ResponseEntity<Void> deleteClass(@PathVariable Integer id) {
        studentClassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
