package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.UpdateUserRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UserDTO;
import com.thuyloiuni.teaching_schedule_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Các API để quản lý tài khoản người dùng (chỉ dành cho ADMIN)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Lấy tất cả người dùng", description = "Truy xuất danh sách tất cả tài khoản người dùng trong hệ thống.")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy người dùng theo ID", description = "Truy xuất thông tin chi tiết của một người dùng cụ thể bằng ID của họ.")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin chi tiết (email, vai trò) của một người dùng hiện có.")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa người dùng", description = "Xóa một tài khoản người dùng. Chỉ có thể thực hiện được nếu người dùng đó không được liên kết với một hồ sơ giảng viên.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
