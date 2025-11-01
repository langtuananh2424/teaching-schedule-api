package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.SemesterDTO;
import com.thuyloiuni.teaching_schedule_api.service.SemesterService;
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
public class SemesterController {

    private final SemesterService semesterService;

    /**
     * API to get a list of distinct academic years (e.g., ["2024-2025", "2023-2024"])
     * Used for the first dropdown in the report filtering UI.
     */
    @GetMapping("/academic-years")
    public ResponseEntity<List<String>> getAcademicYears() {
        List<String> academicYears = semesterService.getAcademicYears();
        return ResponseEntity.ok(academicYears);
    }

    /**
     * API to get a list of semesters based on an academic year.
     * Used for the second dropdown, which is populated after selecting an academic year.
     *
     * @param academicYear The academic year to filter by (e.g., "2023-2024")
     * @return A list of semester DTOs.
     */
    @GetMapping
    public ResponseEntity<List<SemesterDTO>> getSemestersByYear(@RequestParam("academicYear") String academicYear) {
        List<SemesterDTO> semesters = semesterService.getSemestersByAcademicYear(academicYear);
        return ResponseEntity.ok(semesters);
    }
}
