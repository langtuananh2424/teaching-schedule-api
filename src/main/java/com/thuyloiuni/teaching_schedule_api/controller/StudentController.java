package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateStudentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentDTO;
import com.thuyloiuni.teaching_schedule_api.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Các API để quản lý thông tin sinh viên")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Lấy tất cả sinh viên", description = "Truy xuất danh sách tất cả sinh viên trong hệ thống.")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy sinh viên theo ID", description = "Truy xuất thông tin một sinh viên cụ thể bằng ID.")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Integer id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/by-class/{classId}")
    @Operation(summary = "Lấy sinh viên theo lớp học", description = "Truy xuất danh sách tất cả sinh viên thuộc về một lớp học cụ thể.")
    public ResponseEntity<List<StudentDTO>> getStudentsByClass(@PathVariable Integer classId) {
        return ResponseEntity.ok(studentService.getStudentsByClassId(classId));
    }

    @GetMapping("/by-schedule/{scheduleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LECTURER')")
    @Operation(summary = "Lấy sinh viên theo buổi học", description = "Truy xuất danh sách tất cả sinh viên có trong một buổi học cụ thể.")
    public ResponseEntity<List<StudentDTO>> getStudentsByScheduleId(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(studentService.getStudentsByScheduleId(scheduleId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo sinh viên mới", description = "Tạo một sinh viên mới trong hệ thống. Chỉ ADMIN có quyền.")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody CreateStudentDTO createStudentDTO) {
        StudentDTO createdStudent = studentService.createStudent(createStudentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật sinh viên", description = "Cập nhật thông tin một sinh viên đã có. Chỉ ADMIN có quyền.")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Integer id, @Valid @RequestBody CreateStudentDTO createStudentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, createStudentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa sinh viên", description = "Xóa một sinh viên khỏi hệ thống. Chỉ ADMIN có quyền.")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
