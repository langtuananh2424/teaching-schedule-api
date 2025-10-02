package com.thuyloiuni.teaching_schedule_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lecturers") // Base path cho tất cả các endpoint của lecturer
@RequiredArgsConstructor // Lombok để tự động inject dependencies qua constructor
public class LecturerController {

    private final LecturerService lecturerService;

    // POST /api/lecturers -> Tạo một giảng viên mới
    @PostMapping
    public ResponseEntity<LecturerDTO> createLecturer(/*@Valid*/ @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        // @Valid sẽ kích hoạt validation nếu bạn có các annotation validation trên CreateLecturerRequestDTO
        LecturerDTO createdLecturer = lecturerService.createLecturer(lecturerRequestDTO);
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED); // HTTP 201 Created
    }

    // GET /api/lecturers/{id} -> Lấy thông tin giảng viên theo ID
    @GetMapping("/{id}")
    public ResponseEntity<LecturerDTO> getLecturerById(@PathVariable Integer id) {
        return lecturerService.getLecturerById(id)
                .map(ResponseEntity::ok) // Nếu tìm thấy, trả về HTTP 200 OK với lecturerDTO
                .orElseGet(() -> ResponseEntity.notFound().build()); // Nếu không, trả về HTTP 404 Not Found
    }

    // GET /api/lecturers/email/{email} -> Lấy thông tin giảng viên theo email
    @GetMapping("/email/{email}") // Sử dụng path variable rõ ràng hơn là query param cho trường hợp này
    public ResponseEntity<LecturerDTO> getLecturerByEmail(@PathVariable String email) {
        return lecturerService.getLecturerByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/lecturers/code/{code} -> Lấy thông tin giảng viên theo mã giảng viên
    @GetMapping("/code/{code}")
    public ResponseEntity<LecturerDTO> getLecturerByCode(@PathVariable String code) {
        return lecturerService.getLecturerByCode(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/lecturers -> Lấy danh sách tất cả giảng viên
    // Cân nhắc thêm phân trang ở đây nếu danh sách có thể rất lớn
    @GetMapping
    public ResponseEntity<List<LecturerDTO>> getAllLecturers() {
        List<LecturerDTO> lecturers = lecturerService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }

    // GET /api/lecturers/department/{departmentId} -> Lấy danh sách giảng viên theo ID của Khoa
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<LecturerDTO>> getLecturersByDepartmentId(@PathVariable Integer departmentId) {
        List<LecturerDTO> lecturers = lecturerService.getLecturersByDepartmentId(departmentId);
        // Nếu departmentId không tồn tại, service nên ném ResourceNotFoundException
        // và GlobalExceptionHandler sẽ xử lý thành 404.
        // Nếu không có lecturer nào thuộc department đó, sẽ trả về danh sách rỗng với HTTP 200.
        return ResponseEntity.ok(lecturers);
    }

    // GET /api/lecturers/role -> Lấy danh sách giảng viên theo vai trò
    @GetMapping("/role") // Sử dụng @RequestParam để truyền role
    public ResponseEntity<List<LecturerDTO>> getLecturersByRole(@RequestParam("role") String roleName) {
        try {
            RoleType role = RoleType.valueOf(roleName.toUpperCase());
            List<LecturerDTO> lecturers = lecturerService.getLecturersByRole(role);
            return ResponseEntity.ok(lecturers);
        } catch (IllegalArgumentException e) {
            // Nếu roleName không hợp lệ, trả về Bad Request
            // Bạn cũng có thể tạo một custom exception và để GlobalExceptionHandler xử lý
            return ResponseEntity.badRequest().body(null); // Hoặc trả về một message lỗi cụ thể
        }
    }


    // PUT /api/lecturers/{id} -> Cập nhật thông tin giảng viên
    @PutMapping("/{id}")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable Integer id, /*@Valid*/ @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO updatedLecturer = lecturerService.updateLecturer(id, lecturerRequestDTO);
        // Nếu id không tồn tại, service nên ném ResourceNotFoundException (GlobalExceptionHandler xử lý)
        // Nếu có lỗi validation (ví dụ: email/code đã tồn tại), service nên ném IllegalArgumentException (GlobalExceptionHandler xử lý)
        return ResponseEntity.ok(updatedLecturer);
    }

    // DELETE /api/lecturers/{id} -> Xóa giảng viên theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Integer id) {
        lecturerService.deleteLecturer(id);
        // Nếu id không tồn tại, service nên ném ResourceNotFoundException (GlobalExceptionHandler xử lý)
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}
