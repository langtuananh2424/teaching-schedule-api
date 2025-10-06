package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;
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
public class LecturerController {

    private final LecturerService lecturerService;

    // POST /api/lecturers -> Tạo một giảng viên mới
    // Endpoint này được cho phép public trong SecurityConfig để đăng ký
    @PostMapping
    public ResponseEntity<LecturerDTO> createLecturer(@Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO createdLecturer = lecturerService.createLecturer(lecturerRequestDTO);
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED);
    }

    // GET /api/lecturers -> Lấy danh sách tất cả giảng viên
    // Bất kỳ ai đã xác thực đều có thể xem
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LecturerDTO>> getAllLecturers() {
        List<LecturerDTO> lecturers = lecturerService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }

    // GET /api/lecturers/{id} -> Lấy thông tin giảng viên theo ID
    // Bất kỳ ai đã xác thực đều có thể xem
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LecturerDTO> getLecturerById(@PathVariable Integer id) {
        // Không cần xử lý Optional ở đây nữa
        // Nếu không tìm thấy, service sẽ ném ResourceNotFoundException và GlobalExceptionHandler sẽ bắt, trả về 404
        LecturerDTO lecturer = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(lecturer);
    }

    // GET /api/lecturers/by-code/{code} -> Lấy thông tin giảng viên theo mã giảng viên
    @GetMapping("/by-code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LecturerDTO> getLecturerByCode(@PathVariable String code) {
        LecturerDTO lecturer = lecturerService.getLecturerByCode(code);
        return ResponseEntity.ok(lecturer);
    }

    // GET /api/lecturers/by-email/{email} -> Lấy thông tin giảng viên theo email
    @GetMapping("/by-email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LecturerDTO> getLecturerByEmail(@PathVariable String email) {
        LecturerDTO lecturer = lecturerService.getLecturerByEmail(email);
        return ResponseEntity.ok(lecturer);
    }

    // GET /api/lecturers/filter-by -> Lọc giảng viên theo các tiêu chí khác nhau
    // Gộp các endpoint GET theo Department và Role vào một endpoint linh hoạt hơn
    @GetMapping("/filter-by")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LecturerDTO>> getFilteredLecturers(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) RoleType role) {

        if (departmentId != null) {
            return ResponseEntity.ok(lecturerService.getLecturersByDepartmentId(departmentId));
        }
        if (role != null) {
            return ResponseEntity.ok(lecturerService.getLecturersByRole(role));
        }
        // Nếu không có param nào, trả về danh sách rỗng hoặc có thể ném lỗi tùy nghiệp vụ
        return ResponseEntity.ok(List.of());
    }

    // PUT /api/lecturers/{id} -> Cập nhật thông tin giảng viên
    // Chỉ ADMIN mới có quyền
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable Integer id, @Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO updatedLecturer = lecturerService.updateLecturer(id, lecturerRequestDTO);
        return ResponseEntity.ok(updatedLecturer);
    }

    // DELETE /api/lecturers/{id} -> Xóa giảng viên theo ID
    // Chỉ ADMIN mới có quyền
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Integer id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }
}
