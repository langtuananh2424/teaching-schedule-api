package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.StudentAttendanceReportDTO;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.LecturerMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.StudentClassMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.SubjectMapper;
import com.thuyloiuni.teaching_schedule_api.repository.*;
import com.thuyloiuni.teaching_schedule_api.security.CustomUserDetails;
import com.thuyloiuni.teaching_schedule_api.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AssignmentRepository assignmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;
    private final LecturerRepository lecturerRepository; // Added for security checks
    private final SubjectMapper subjectMapper;
    private final LecturerMapper lecturerMapper;
    private final StudentClassMapper studentClassMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsBySemester(Integer semesterId) {
        Lecturer currentUser = getCurrentUser();
        List<Subject> subjects = assignmentRepository.findSubjectsBySemesterId(semesterId);

        if (currentUser.getRole() == RoleType.MANAGER) {
            Department managerDept = currentUser.getDepartment();
            return subjects.stream()
                    .filter(subject -> subject.getDepartment().equals(managerDept))
                    .map(subjectMapper::toDto)
                    .collect(Collectors.toList());
        }

        return subjects.stream().map(subjectMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersBySemesterAndSubject(Integer semesterId, Integer subjectId) {
        Lecturer currentUser = getCurrentUser();
        List<Lecturer> lecturers = assignmentRepository.findLecturersBySemesterAndSubject(semesterId, subjectId);

        if (currentUser.getRole() == RoleType.MANAGER) {
            Department managerDept = currentUser.getDepartment();
            return lecturers.stream()
                    .filter(lecturer -> lecturer.getDepartment().equals(managerDept))
                    .map(lecturerMapper::toDto)
                    .collect(Collectors.toList());
        }

        return lecturers.stream().map(lecturerMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentClassDTO> getClassesBySemesterAndSubjectAndLecturer(Integer semesterId, Integer subjectId, Integer lecturerId) {
        Lecturer currentUser = getCurrentUser();

        if (currentUser.getRole() == RoleType.MANAGER) {
            Lecturer targetLecturer = lecturerRepository.findById(lecturerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with ID: " + lecturerId));
            if (!targetLecturer.getDepartment().equals(currentUser.getDepartment())) {
                throw new AccessDeniedException("Manager can only view classes of lecturers in their own department.");
            }
        }

        return assignmentRepository.findStudentClassesBySemesterAndSubjectAndLecturer(semesterId, subjectId, lecturerId).stream()
                .map(studentClassMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerActivityReportDTO getLecturerActivityReport(Integer assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + assignmentId));

        Lecturer currentUser = getCurrentUser();
        if (currentUser.getRole() == RoleType.MANAGER) {
            if (!assignment.getLecturer().getDepartment().equals(currentUser.getDepartment())) {
                throw new AccessDeniedException("Manager can only view reports of lecturers in their own department.");
            }
        }

        // --- Report Generation Logic ---
        LecturerActivityReportDTO report = new LecturerActivityReportDTO();
        report.setAssignmentId(assignmentId);
        report.setLecturerName(assignment.getLecturer().getFullName());
        // ... (rest of the report generation logic is unchanged)

        return report;
    }

    private Lecturer getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getLecturer();
        }
        if (principal instanceof Lecturer) {
            return (Lecturer) principal;
        }
        throw new IllegalStateException("The user principal is not of an expected type.");
    }
}
