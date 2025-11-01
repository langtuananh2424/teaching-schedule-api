package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;
import com.thuyloiuni.teaching_schedule_api.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // --- Endpoints for filtering dropdowns ---

    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjectsBySemester(@RequestParam Integer semesterId) {
        List<SubjectDTO> subjects = reportService.getSubjectsBySemester(semesterId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/lecturers")
    public ResponseEntity<List<LecturerDTO>> getLecturersForReport(
            @RequestParam Integer semesterId,
            @RequestParam Integer subjectId) {
        List<LecturerDTO> lecturers = reportService.getLecturersBySemesterAndSubject(semesterId, subjectId);
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/classes")
    public ResponseEntity<List<StudentClassDTO>> getClassesForReport(
            @RequestParam Integer semesterId,
            @RequestParam Integer subjectId,
            @RequestParam Integer lecturerId) {
        List<StudentClassDTO> classes = reportService.getClassesBySemesterAndSubjectAndLecturer(semesterId, subjectId, lecturerId);
        return ResponseEntity.ok(classes);
    }

    // --- Endpoint for generating the final report ---

    @GetMapping("/lecturer-activity")
    public ResponseEntity<LecturerActivityReportDTO> getLecturerActivityReport(@RequestParam Integer assignmentId) {
        LecturerActivityReportDTO report = reportService.getLecturerActivityReport(assignmentId);
        return ResponseEntity.ok(report);
    }
}
