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
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Các API để quản lý việc điểm danh của sinh viên")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/by-schedule/{scheduleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'MANAGER')")
    @Operation(summary = "Lấy kết quả điểm danh của buổi học", description = "Truy xuất danh sách kết quả điểm danh của một buổi học cụ thể bằng ID buổi học.")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByScheduleId(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByScheduleId(scheduleId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(summary = "Thực hiện điểm danh cho buổi học", description = "Gửi lên danh sách sinh viên và trạng thái điểm danh tương ứng cho một buổi học. Chỉ LECTURER hoặc ADMIN có quyền.")
    public ResponseEntity<List<AttendanceDTO>> takeAttendance(@Valid @RequestBody TakeAttendanceDTO takeAttendanceDTO) {
        List<AttendanceDTO> result = attendanceService.takeAttendance(takeAttendanceDTO);
        return ResponseEntity.ok(result);
    }
}
