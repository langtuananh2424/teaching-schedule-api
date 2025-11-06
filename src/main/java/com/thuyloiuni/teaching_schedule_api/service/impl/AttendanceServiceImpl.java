package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.AttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.dto.TakeAttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Attendance;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.Student;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.AttendanceMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AttendanceRepository;
import com.thuyloiuni.teaching_schedule_api.repository.MakeupSessionRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.repository.StudentRepository;
import com.thuyloiuni.teaching_schedule_api.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final MakeupSessionRepository makeupSessionRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceBySessionId(Integer sessionId) {
        if (!scheduleRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + sessionId);
        }
        List<Attendance> attendances = attendanceRepository.findBySchedule_SessionId(sessionId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional
    public List<AttendanceDTO> takeAttendance(Integer sessionId, List<TakeAttendanceDTO> attendanceList) {
        Schedule schedule = scheduleRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + sessionId));

        List<Integer> studentIds = attendanceList.stream()
                .map(TakeAttendanceDTO::getStudentId)
                .collect(Collectors.toList());

        Map<Integer, Student> studentMap = studentRepository.findAllById(studentIds).stream()
                .collect(Collectors.toMap(Student::getStudentId, student -> student));

        for (Integer studentId : studentIds) {
            if (!studentMap.containsKey(studentId)) {
                throw new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + studentId);
            }
        }

        List<Attendance> savedAttendances = new ArrayList<>();
        for (TakeAttendanceDTO dto : attendanceList) {
            Attendance attendance = attendanceRepository.findBySchedule_SessionIdAndStudent_StudentId(sessionId, dto.getStudentId())
                    .orElse(new Attendance());

            attendance.setSchedule(schedule);
            attendance.setStudent(studentMap.get(dto.getStudentId()));
            attendance.setIsPresent(dto.getIsPresent());
            attendance.setTimestamp(LocalDateTime.now());

            savedAttendances.add(attendanceRepository.save(attendance));
        }

        // Update the schedule status after taking attendance
        boolean isMakeupSession = makeupSessionRepository.existsByMakeupSchedule_SessionId(schedule.getSessionId());
        if (isMakeupSession) {
            schedule.setStatus(ScheduleStatus.MAKEUP_TAUGHT);
        } else {
            schedule.setStatus(ScheduleStatus.TAUGHT);
        }
        scheduleRepository.save(schedule);

        return attendanceMapper.toDtoList(savedAttendances);
    }
}
