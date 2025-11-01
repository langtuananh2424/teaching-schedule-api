package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;

import java.util.List;

public interface ReportService {
    // --- Filtering Methods ---
    List<SubjectDTO> getSubjectsBySemester(Integer semesterId);
    List<LecturerDTO> getLecturersBySemesterAndSubject(Integer semesterId, Integer subjectId);
    List<StudentClassDTO> getClassesBySemesterAndSubjectAndLecturer(Integer semesterId, Integer subjectId, Integer lecturerId);

    // --- Report Generation Method ---
    LecturerActivityReportDTO getLecturerActivityReport(Integer assignmentId);
}
