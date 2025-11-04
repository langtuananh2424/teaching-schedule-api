package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;

import java.util.List;

public interface ReportService {
    
    List<SubjectDTO> getSubjectsBySemester(Integer semesterId);

    List<LecturerDTO> getLecturersBySemesterAndSubject(Integer semesterId, Integer subjectId);

    List<StudentClassDTO> getClassesBySemesterAndSubjectAndLecturer(Integer semesterId, Integer subjectId, Integer lecturerId);

    /**
     * Generates a detailed activity report for a specific lecturer assignment, found by the given parameters.
     * A security check is performed to ensure a MANAGER can only access reports for lecturers in their own department.
     */
    LecturerActivityReportDTO getLecturerActivityReport(Integer semesterId, Integer subjectId, Integer lecturerId, Integer classId);
}
