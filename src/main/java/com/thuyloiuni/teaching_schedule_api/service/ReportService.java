package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;

import java.util.List;

public interface ReportService {
    
    /**
     * Retrieves subjects for filtering based on user role.
     * - ADMIN sees all subjects.
     * - MANAGER sees only subjects belonging to their department.
     */
    List<SubjectDTO> getSubjectsBySemester(Integer semesterId);

    /**
     * Retrieves lecturers for filtering based on user role.
     * - ADMIN sees all lecturers.
     * - MANAGER sees only lecturers from their department.
     */
    List<LecturerDTO> getLecturersBySemesterAndSubject(Integer semesterId, Integer subjectId);

    /**
     * Retrieves student classes for filtering based on user role.
     * - ADMIN sees all classes.
     * - MANAGER sees only classes taught by lecturers from their department.
     */
    List<StudentClassDTO> getClassesBySemesterAndSubjectAndLecturer(Integer semesterId, Integer subjectId, Integer lecturerId);

    /**
     * Generates a detailed activity report for a specific lecturer assignment.
     * A security check is performed to ensure a MANAGER can only access reports for lecturers in their own department.
     */
    LecturerActivityReportDTO getLecturerActivityReport(Integer assignmentId);
}
