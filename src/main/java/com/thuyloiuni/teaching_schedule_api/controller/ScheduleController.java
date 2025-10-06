package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.service.ScheduleService;
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
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Lấy tất cả các buổi học. Chỉ ADMIN có quyền.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    /**
     * Lấy một buổi học cụ thể theo ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    /**
     * Lấy danh sách các buổi học của một phân công cụ thể.
     */
    @GetMapping("/by-assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByAssignment(@PathVariable Integer assignmentId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByAssignment(assignmentId));
    }

    /**
     * Tạo một buổi học mới. Chỉ ADMIN có quyền.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody CreateScheduleDTO createDto) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(createDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    /**
     * Cập nhật một buổi học. Chỉ ADMIN có quyền.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Integer id, @Valid @RequestBody CreateScheduleDTO updateDto) {
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, updateDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    /**
     * Xóa một buổi học. Chỉ ADMIN có quyền.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
