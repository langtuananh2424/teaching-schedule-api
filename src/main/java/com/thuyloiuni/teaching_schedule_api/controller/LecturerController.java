package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdatePasswordDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.User;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.security.CustomUserDetails;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
@Tag(name = "Lecturer", description = "Các API để quản lý thông tin giảng viên")
public class LecturerController {

    private final LecturerService lecturerService;

    @PostMapping
    @Operation(summary = "Tạo giảng viên mới", description = "Tạo một tài khoản giảng viên mới. Mặc định, endpoint này có thể cần được bảo vệ chỉ cho ADMIN.")
    public ResponseEntity<LecturerDTO> createLecturer(@Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO createdLecturer = lecturerService.createLecturer(lecturerRequestDTO);
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy tất cả giảng viên", description = "Truy xuất danh sách tất cả giảng viên trong hệ thống. Yêu cầu đã xác thực.")
    public ResponseEntity<List<LecturerDTO>> getAllLecturers() {
        List<LecturerDTO> lecturers = lecturerService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy giảng viên theo ID", description = "Truy xuất thông tin chi tiết của một giảng viên bằng ID. Yêu cầu đã xác thực.")
    public ResponseEntity<LecturerDTO> getLecturerById(@PathVariable Integer id) {
        LecturerDTO lecturer = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(lecturer);
    }

    @GetMapping("/by-code/{code}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy giảng viên theo mã giảng viên", description = "Truy xuất thông tin chi tiết của một giảng viên bằng mã giảng viên. Yêu cầu đã xác thực.")
    public ResponseEntity<LecturerDTO> getLecturerByCode(@PathVariable String code) {
        LecturerDTO lecturer = lecturerService.getLecturerByCode(code);
        return ResponseEntity.ok(lecturer);
    }

    @GetMapping("/by-email/{email}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy giảng viên theo email", description = "Truy xuất thông tin chi tiết của một giảng viên bằng địa chỉ email. Yêu cầu đã xác thực.")
    public ResponseEntity<LecturerDTO> getLecturerByEmail(@PathVariable String email) {
        LecturerDTO lecturer = lecturerService.getLecturerByEmail(email);
        return ResponseEntity.ok(lecturer);
    }

    @GetMapping("/filter-by")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lọc giảng viên theo khoa hoặc vai trò", description = "Lọc và truy xuất danh sách giảng viên dựa trên ID khoa hoặc vai trò. Yêu cầu đã xác thực.")
    public ResponseEntity<List<LecturerDTO>> getFilteredLecturers(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) RoleType role) {

        if (departmentId != null) {
            return ResponseEntity.ok(lecturerService.getLecturersByDepartmentId(departmentId));
        }
        if (role != null) {
            return ResponseEntity.ok(lecturerService.getLecturersByRole(role));
        }
        return ResponseEntity.ok(List.of());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin giảng viên", description = "Cập nhật thông tin chi tiết cho một giảng viên. Chỉ ADMIN có quyền.")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable Integer id, @Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO updatedLecturer = lecturerService.updateLecturer(id, lecturerRequestDTO);
        return ResponseEntity.ok(updatedLecturer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa giảng viên", description = "Xóa một giảng viên khỏi hệ thống. Chỉ ADMIN có quyền.")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Integer id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cập nhật mật khẩu của giảng viên", description = "Cho phép người dùng tự cập nhật mật khẩu của mình, hoặc ADMIN cập nhật mật khẩu cho bất kỳ ai.")
    public ResponseEntity<Void> updatePassword(@PathVariable Integer id, 
                                           @Valid @RequestBody UpdatePasswordDTO passwordDTO,
                                           @AuthenticationPrincipal CustomUserDetails currentUserDetails) {
        User currentUser = currentUserDetails.getUser();
        Lecturer lecturerInfo = currentUser.getLecturer();

        boolean isOwner = (lecturerInfo != null && lecturerInfo.getLecturerId().equals(id));
        boolean isAdmin = currentUser.getRole() == RoleType.ADMIN;
        
        if (!isOwner && !isAdmin) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); 
        }

        lecturerService.updatePassword(id, passwordDTO);
        return ResponseEntity.ok().build();
    }
}
