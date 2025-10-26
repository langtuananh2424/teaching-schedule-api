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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    /**
     * NEW ENDPOINT: Lấy danh sách các buổi học của một giảng viên cụ thể qua email.
     */
    @GetMapping("/lecturer/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByLecturerEmail(@PathVariable String email) {
        return ResponseEntity.ok(scheduleService.getSchedulesByLecturerEmail(email));
    }

    @GetMapping("/by-assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByAssignment(@PathVariable Integer assignmentId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByAssignment(assignmentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody CreateScheduleDTO createDto) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(createDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Integer id, @Valid @RequestBody CreateScheduleDTO updateDto) {
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, updateDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
