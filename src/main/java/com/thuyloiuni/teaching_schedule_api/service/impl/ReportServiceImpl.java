package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.StudentAttendanceReportDTO;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.LecturerMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.ScheduleMapper;
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
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;
    private final SubjectMapper subjectMapper;
    private final LecturerMapper lecturerMapper;
    private final StudentClassMapper studentClassMapper;
    private final ScheduleMapper scheduleMapper;

    // ... other methods ...
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsBySemester(Integer semesterId) {
        User currentUser = getCurrentUser();
        List<Subject> subjects = assignmentRepository.findSubjectsBySemesterId(semesterId);

        if (currentUser.getRole() == RoleType.MANAGER) {
            Lecturer managerLecturerInfo = currentUser.getLecturer();
            if (managerLecturerInfo == null) {
                throw new IllegalStateException("A user with MANAGER role must have an associated lecturer profile.");
            }
            Department managerDept = managerLecturerInfo.getDepartment();
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
        User currentUser = getCurrentUser();
        List<Lecturer> lecturers = assignmentRepository.findLecturersBySemesterAndSubject(semesterId, subjectId);

        if (currentUser.getRole() == RoleType.MANAGER) {
            Lecturer managerLecturerInfo = currentUser.getLecturer();
            if (managerLecturerInfo == null) {
                throw new IllegalStateException("A user with MANAGER role must have an associated lecturer profile.");
            }
            Department managerDept = managerLecturerInfo.getDepartment();
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
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == RoleType.MANAGER) {
            Lecturer managerLecturerInfo = currentUser.getLecturer();
            if (managerLecturerInfo == null) {
                throw new IllegalStateException("A user with MANAGER role must have an associated lecturer profile.");
            }
            Lecturer targetLecturer = lecturerRepository.findById(lecturerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with ID: " + lecturerId));
            if (!targetLecturer.getDepartment().equals(managerLecturerInfo.getDepartment())) {
                throw new AccessDeniedException("Manager can only view classes of lecturers in their own department.");
            }
        }

        return assignmentRepository.findStudentClassesBySemesterAndSubjectAndLecturer(semesterId, subjectId, lecturerId).stream()
                .map(studentClassMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerActivityReportDTO getLecturerActivityReport(Integer semesterId, Integer subjectId, Integer lecturerId, Integer classId) {
        Assignment assignment = assignmentRepository.findBySemester_IdAndSubject_SubjectIdAndLecturer_LecturerIdAndStudentClass_ClassId(semesterId, subjectId, lecturerId, classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công giảng dạy phù hợp với các tiêu chí đã chọn."));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() == RoleType.MANAGER) {
            Lecturer managerLecturerInfo = currentUser.getLecturer();
            if (managerLecturerInfo == null) {
                throw new IllegalStateException("A user with MANAGER role must have an associated lecturer profile.");
            }
            if (!assignment.getLecturer().getDepartment().equals(managerLecturerInfo.getDepartment())) {
                throw new AccessDeniedException("Manager can only view reports of lecturers in their own department.");
            }
        }

        // --- Main Report Generation Logic ---
        LecturerActivityReportDTO report = new LecturerActivityReportDTO();
        populateBasicInfo(report, assignment);

        List<Schedule> schedules = scheduleRepository.findByAssignment_AssignmentId(assignment.getAssignmentId());
        report.setSchedules(scheduleMapper.toDtoList(schedules));

        populatePeriodStats(report, assignment, schedules);
        populateAttendanceReports(report, assignment, schedules);

        return report;
    }

    private void populateBasicInfo(LecturerActivityReportDTO report, Assignment assignment) {
        report.setAssignmentId(assignment.getAssignmentId());
        report.setLecturerName(assignment.getLecturer().getFullName());
        report.setSubjectName(assignment.getSubject().getSubjectName());
        report.setClassName(assignment.getStudentClass().getClassName());
        report.setSemesterName(assignment.getSemester().getName());
        report.setAcademicYear(assignment.getSemester().getAcademicYear());
    }

    private void populatePeriodStats(LecturerActivityReportDTO report, Assignment assignment, List<Schedule> schedules) {
        Subject subject = assignment.getSubject();
        LecturerActivityReportDTO.Periods planned = new LecturerActivityReportDTO.Periods(
                subject.getTheoryPeriods() + subject.getPracticePeriods(),
                subject.getTheoryPeriods(),
                subject.getPracticePeriods(), 0, 0
        );

        int regularTaught = 0;
        int makeupTaught = 0;

        for (Schedule schedule : schedules) {
            if (schedule.getStatus() == ScheduleStatus.TAUGHT) {
                regularTaught += (schedule.getEndPeriod() - schedule.getStartPeriod() + 1);
            } else if (schedule.getStatus() == ScheduleStatus.MAKEUP_TAUGHT) {
                makeupTaught += (schedule.getEndPeriod() - schedule.getStartPeriod() + 1);
            }
        }

        LecturerActivityReportDTO.Periods taught = new LecturerActivityReportDTO.Periods(
                regularTaught + makeupTaught, 0, 0, regularTaught, makeupTaught
        );

        report.setPlannedPeriods(planned);
        report.setTaughtPeriods(taught);
    }

    private void populateAttendanceReports(LecturerActivityReportDTO report, Assignment assignment, List<Schedule> taughtSchedulesOnly) {
        List<Student> students = studentRepository.findByStudentClass(assignment.getStudentClass());
        List<Attendance> allAttendances = attendanceRepository.findBySchedule_Assignment_AssignmentId(assignment.getAssignmentId());

        Map<Integer, List<Attendance>> attendanceByStudentId = allAttendances.stream()
                .collect(Collectors.groupingBy(att -> att.getStudent().getStudentId()));

        List<StudentAttendanceReportDTO> studentReports = new ArrayList<>();

        for (Student student : students) {
            StudentAttendanceReportDTO studentReport = new StudentAttendanceReportDTO();
            studentReport.setStudentId(student.getStudentId().toString());
            studentReport.setStudentName(student.getFullName());
            studentReport.setStudentCode(student.getStudentCode());

            List<Attendance> studentAttendances = attendanceByStudentId.getOrDefault(student.getStudentId(), new ArrayList<>());
            List<StudentAttendanceReportDTO.AttendanceDetail> details = studentAttendances.stream().map(att -> {
                StudentAttendanceReportDTO.AttendanceDetail detail = new StudentAttendanceReportDTO.AttendanceDetail();
                detail.setSessionId(att.getSchedule().getSessionId());
                detail.setSessionDate(att.getSchedule().getSessionDate());
                detail.setPresent(att.getIsPresent());
                return detail;
            }).collect(Collectors.toList());

            int attended = (int) studentAttendances.stream().filter(Attendance::getIsPresent).count();

            studentReport.setAttendanceDetails(details);
            studentReport.setTotalSessions(taughtSchedulesOnly.size()); // Total taught sessions
            studentReport.setAttendedSessions(attended);
            studentReport.setAbsentSessions(taughtSchedulesOnly.size() - attended);
            studentReports.add(studentReport);
        }
        report.setStudentAttendanceReports(studentReports);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        throw new IllegalStateException("The user principal is not of an expected type.");
    }
}
