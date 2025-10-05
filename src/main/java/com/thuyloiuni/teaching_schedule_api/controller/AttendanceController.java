package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.AttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.dto.TakeAttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * API để lấy kết quả điểm danh của một buổi học cụ thể.
     * Cả ADMIN và LECTURER đều có thể xem.
     */
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceBySession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySessionId(sessionId));
    }

    /**
     * API để thực hiện điểm danh cho một buổi học.
     * Chỉ LECTURER mới có quyền.
     *
     * @param sessionId ID của buổi học cần điểm danh.
     * @param attendanceList Danh sách trạng thái điểm danh của sinh viên.
     */
    @PostMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<AttendanceDTO>> takeAttendance(
            @PathVariable Integer sessionId,
            @Valid @RequestBody List<TakeAttendanceDTO> attendanceList) {
        List<AttendanceDTO> result = attendanceService.takeAttendance(sessionId, attendanceList);
        return ResponseEntity.ok(result);
    }
}
