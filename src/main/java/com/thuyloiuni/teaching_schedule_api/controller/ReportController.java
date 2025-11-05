package com.thuyloiuni.teaching_schedule_api.controller;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;
import com.thuyloiuni.teaching_schedule_api.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "Các API để tạo và truy xuất các loại báo cáo")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy môn học theo học kỳ để báo cáo", description = "Truy xuất danh sách các môn học trong một học kỳ cụ thể. ADMIN thấy tất cả, MANAGER chỉ thấy các môn thuộc khoa mình.")
    public ResponseEntity<List<SubjectDTO>> getSubjectsBySemester(@RequestParam Integer semesterId) {
        List<SubjectDTO> subjects = reportService.getSubjectsBySemester(semesterId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/lecturers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy giảng viên theo học kỳ và môn học để báo cáo", description = "Truy xuất danh sách giảng viên dạy một môn học trong một học kỳ. ADMIN thấy tất cả, MANAGER chỉ thấy giảng viên thuộc khoa mình.")
    public ResponseEntity<List<LecturerDTO>> getLecturersForReport(
            @RequestParam Integer semesterId,
            @RequestParam Integer subjectId) {
        List<LecturerDTO> lecturers = reportService.getLecturersBySemesterAndSubject(semesterId, subjectId);
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy lớp học theo học kỳ, môn học và giảng viên", description = "Truy xuất danh sách các lớp học do một giảng viên cụ thể dạy. ADMIN thấy tất cả, MANAGER chỉ thấy các lớp của giảng viên thuộc khoa mình.")
    public ResponseEntity<List<StudentClassDTO>> getClassesForReport(
            @RequestParam Integer semesterId,
            @RequestParam Integer subjectId,
            @RequestParam Integer lecturerId) {
        List<StudentClassDTO> classes = reportService.getClassesBySemesterAndSubjectAndLecturer(semesterId, subjectId, lecturerId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/lecturer-activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Tạo báo cáo hoạt động của giảng viên", description = "Tạo báo cáo chi tiết hoạt động của giảng viên dựa trên học kỳ, môn học, giảng viên và lớp học. ADMIN có thể xem của bất kỳ ai, MANAGER chỉ có thể xem của giảng viên trong khoa mình.")
    public ResponseEntity<LecturerActivityReportDTO> getLecturerActivityReport(
            @RequestParam Integer semesterId,
            @RequestParam Integer subjectId,
            @RequestParam Integer lecturerId,
            @RequestParam Integer classId) {
        LecturerActivityReportDTO report = reportService.getLecturerActivityReport(semesterId, subjectId, lecturerId, classId);
        return ResponseEntity.ok(report);
    }
}
