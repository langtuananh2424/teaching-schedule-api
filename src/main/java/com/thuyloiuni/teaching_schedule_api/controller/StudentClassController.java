// src/main/java/com/thuyloiuni/teaching_schedule_api/controller/StudentClassController.java
package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.service.StudentClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-classes") // Path phù hợp với tên Entity
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassService;

    @GetMapping
    public ResponseEntity<List<StudentClassDTO>> getAllClasses() {
        return ResponseEntity.ok(studentClassService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentClassDTO> getClassById(@PathVariable Integer id) {
        return ResponseEntity.ok(studentClassService.getClassById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentClassDTO> createClass(@Valid @RequestBody StudentClassDTO studentClassDTO) {
        StudentClassDTO createdClass = studentClassService.createClass(studentClassDTO);
        return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentClassDTO> updateClass(@PathVariable Integer id, @Valid @RequestBody StudentClassDTO studentClassDTO) {
        StudentClassDTO updatedClass = studentClassService.updateClass(id, studentClassDTO);
        return ResponseEntity.ok(updatedClass);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClass(@PathVariable Integer id) {
        studentClassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
