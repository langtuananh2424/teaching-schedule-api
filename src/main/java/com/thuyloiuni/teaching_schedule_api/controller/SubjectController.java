package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.service.SubjectService;import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * GET /api/subjects -> Lấy danh sách tất cả môn học.
     * Bất kỳ ai đã đăng nhập đều có thể xem.
     */
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        List<SubjectDTO> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    /**
     * GET /api/subjects/{id} -> Lấy thông tin một môn học theo ID.
     * Bất kỳ ai đã đăng nhập đều có thể xem.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable int id) {
        SubjectDTO subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    /**
     * POST /api/subjects -> Tạo một môn học mới.
     * Chỉ ADMIN mới có quyền tạo.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Phân quyền: Chỉ ADMIN
    public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody SubjectDTO subjectDTO) {
        SubjectDTO createdSubject = subjectService.createSubject(subjectDTO);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    /**
     * PUT /api/subjects/{id} -> Cập nhật thông tin một môn học.
     * Chỉ ADMIN mới có quyền cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Phân quyền: Chỉ ADMIN
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable int id, @Valid @RequestBody SubjectDTO subjectDTO) {
        SubjectDTO updatedSubject = subjectService.updateSubject(id, subjectDTO);
        return ResponseEntity.ok(updatedSubject);
    }

    /**
     * DELETE /api/subjects/{id} -> Xóa một môn học.
     * Chỉ ADMIN mới có quyền xóa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Phân quyền: Chỉ ADMIN
    public ResponseEntity<Void> deleteSubject(@PathVariable int id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }
}
