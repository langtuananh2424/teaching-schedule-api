package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.AttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.dto.TakeAttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Các API để quản lý việc điểm danh của sinh viên")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Lấy kết quả điểm danh của buổi học", description = "Truy xuất danh sách kết quả điểm danh của một buổi học cụ thể bằng ID buổi học.")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceBySession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySessionId(sessionId));
    }

    @PostMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(summary = "Thực hiện điểm danh cho buổi học", description = "Gửi lên danh sách sinh viên và trạng thái điểm danh tương ứng cho một buổi học. Chỉ LECTURER có quyền.")
    public ResponseEntity<List<AttendanceDTO>> takeAttendance(
            @PathVariable Integer sessionId,
            @Valid @RequestBody List<TakeAttendanceDTO> attendanceList) {
        List<AttendanceDTO> result = attendanceService.takeAttendance(sessionId, attendanceList);
        return ResponseEntity.ok(result);
    }
}
