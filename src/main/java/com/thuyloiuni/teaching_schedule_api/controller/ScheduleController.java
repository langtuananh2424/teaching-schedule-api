package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.service.ScheduleService;
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
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "Các API để quản lý lịch học và các buổi học")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả lịch học", description = "Truy xuất danh sách tất cả các buổi học trong hệ thống. Chỉ ADMIN có quyền.")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @Operation(summary = "Lấy lịch học theo ID", description = "Truy xuất thông tin một buổi học cụ thể bằng ID.")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping("/lecturer/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'MANAGER')")
    @Operation(summary = "Lấy lịch học theo email giảng viên", description = "Truy xuất danh sách các buổi học của một giảng viên cụ thể bằng email.")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByLecturerEmail(@PathVariable String email) {
        return ResponseEntity.ok(scheduleService.getSchedulesByLecturerEmail(email));
    }

    @GetMapping("/by-assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'MANAGER')")
    @Operation(summary = "Lấy lịch học theo phân công giảng dạy", description = "Truy xuất danh sách tất cả các buổi học thuộc về một phân công giảng dạy cụ thể.")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByAssignment(@PathVariable Integer assignmentId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByAssignment(assignmentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Tạo lịch học mới", description = "Tạo một buổi học mới trong lịch trình. Chỉ ADMIN hoặc MANAGER có quyền.")
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody CreateScheduleDTO createDto) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(createDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cập nhật lịch học", description = "Cập nhật thông tin một buổi học đã có. Chỉ ADMIN hoặc MANAGER có quyền.")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Integer id, @Valid @RequestBody CreateScheduleDTO updateDto) {
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, updateDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa lịch học", description = "Xóa một buổi học khỏi hệ thống. Chỉ ADMIN có quyền.")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
