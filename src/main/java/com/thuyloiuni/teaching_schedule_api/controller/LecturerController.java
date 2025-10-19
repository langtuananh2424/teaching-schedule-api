package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdatePasswordDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.security.CustomUserDetails;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;
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
public class LecturerController {

    private final LecturerService lecturerService;

    @PostMapping
    public ResponseEntity<LecturerDTO> createLecturer(@Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO createdLecturer = lecturerService.createLecturer(lecturerRequestDTO);
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LecturerDTO>> getAllLecturers() {
        List<LecturerDTO> lecturers = lecturerService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LecturerDTO> getLecturerById(@PathVariable Integer id) {
        LecturerDTO lecturer = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(lecturer);
    }

    @GetMapping("/by-code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LecturerDTO> getLecturerByCode(@PathVariable String code) {
        LecturerDTO lecturer = lecturerService.getLecturerByCode(code);
        return ResponseEntity.ok(lecturer);
    }

    @GetMapping("/by-email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LecturerDTO> getLecturerByEmail(@PathVariable String email) {
        LecturerDTO lecturer = lecturerService.getLecturerByEmail(email);
        return ResponseEntity.ok(lecturer);
    }

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
        return ResponseEntity.ok(List.of());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable Integer id, @Valid @RequestBody CreateLecturerRequestDTO lecturerRequestDTO) {
        LecturerDTO updatedLecturer = lecturerService.updateLecturer(id, lecturerRequestDTO);
        return ResponseEntity.ok(updatedLecturer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Integer id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePassword(@PathVariable Integer id, 
                                           @Valid @RequestBody UpdatePasswordDTO passwordDTO,
                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        // A user can only change their own password, unless they are an admin.
        if (!currentUser.getId().equals(id.longValue()) && !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); 
        }

        lecturerService.updatePassword(id, passwordDTO);
        return ResponseEntity.ok().build();
    }
}
