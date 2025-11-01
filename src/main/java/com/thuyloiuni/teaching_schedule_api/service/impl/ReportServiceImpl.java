package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.LecturerActivityReportDTO;
import com.thuyloiuni.teaching_schedule_api.dto.report.StudentAttendanceReportDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Assignment;
import com.thuyloiuni.teaching_schedule_api.entity.Student;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.LecturerMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.StudentClassMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.SubjectMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AssignmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.AttendanceRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.service.ReportService;
import lombok.RequiredArgsConstructor;
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
    private final SubjectMapper subjectMapper;
    private final LecturerMapper lecturerMapper;
    private final StudentClassMapper studentClassMapper;

    // ... existing methods for filtering ...

    @Override
    @Transactional(readOnly = true)
    public LecturerActivityReportDTO getLecturerActivityReport(Integer assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + assignmentId));

        // 1. Basic Info
        LecturerActivityReportDTO report = new LecturerActivityReportDTO();
        report.setAssignmentId(assignmentId);
        report.setLecturerName(assignment.getLecturer().getFullName());
        report.setSubjectName(assignment.getSubject().getSubjectName());
        report.setClassName(assignment.getStudentClass().getClassName());
        report.setSemesterName(assignment.getSemester().getName());
        report.setAcademicYear(assignment.getSemester().getAcademicYear());

        // 2. Planned Periods
        int plannedTheory = assignment.getSubject().getTheoryPeriods();
        int plannedPractice = assignment.getSubject().getPracticePeriods();
        report.setPlannedPeriods(new LecturerActivityReportDTO.Periods(plannedTheory + plannedPractice, plannedTheory, plannedPractice, 0, 0));

        // 3. Taught Periods
        int taughtPeriods = scheduleRepository.sumTaughtPeriodsByAssignmentId(assignmentId);
        report.setTaughtPeriods(new LecturerActivityReportDTO.Periods(taughtPeriods, 0, 0, taughtPeriods, 0));

        // 4. Student Attendance Report
        List<Student> students = new ArrayList<>(assignment.getStudentClass().getStudents());
        int totalSessions = scheduleRepository.findByAssignment_AssignmentId(assignmentId).size();
        Map<Integer, Long> attendedCounts = attendanceRepository.countAttendedSessionsByStudentForAssignment(assignmentId)
                .stream()
                .collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));

        List<StudentAttendanceReportDTO> studentReports = students.stream().map(student -> {
            int attended = attendedCounts.getOrDefault(student.getStudentId(), 0L).intValue();
            double absencePercentage = (totalSessions > 0) ? ((double) (totalSessions - attended) / totalSessions) * 100 : 0;
            return new StudentAttendanceReportDTO(student.getStudentCode(), student.getFullName(), totalSessions, attended, absencePercentage);
        }).collect(Collectors.toList());

        report.setStudentAttendanceReports(studentReports);

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsBySemester(Integer semesterId) {
        return assignmentRepository.findSubjectsBySemesterId(semesterId).stream()
            .map(subjectMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersBySemesterAndSubject(Integer semesterId, Integer subjectId) {
        return assignmentRepository.findLecturersBySemesterAndSubject(semesterId, subjectId).stream()
            .map(lecturerMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentClassDTO> getClassesBySemesterAndSubjectAndLecturer(Integer semesterId, Integer subjectId, Integer lecturerId) {
        return assignmentRepository.findStudentClassesBySemesterAndSubjectAndLecturer(semesterId, subjectId, lecturerId).stream()
            .map(studentClassMapper::toDto)
            .collect(Collectors.toList());
    }
}
