package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.SemesterDTO;
import com.thuyloiuni.teaching_schedule_api.service.SemesterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
@Tag(name = "Semester", description = "Các API để quản lý thông tin học kỳ và năm học")
public class SemesterController {

    private final SemesterService semesterService;

    @GetMapping("/academic-years")
    @Operation(summary = "Lấy danh sách các năm học", description = "Truy xuất danh sách các năm học duy nhất (ví dụ: \"2024-2025\"). Dùng để điền vào bộ lọc báo cáo.")
    public ResponseEntity<List<String>> getAcademicYears() {
        List<String> academicYears = semesterService.getAcademicYears();
        return ResponseEntity.ok(academicYears);
    }

    @GetMapping
    @Operation(summary = "Lấy học kỳ theo năm học", description = "Truy xuất danh sách các học kỳ dựa trên một năm học cụ thể.")
    public ResponseEntity<List<SemesterDTO>> getSemestersByYear(@RequestParam("academicYear") String academicYear) {
        List<SemesterDTO> semesters = semesterService.getSemestersByAcademicYear(academicYear);
        return ResponseEntity.ok(semesters);
    }
}
