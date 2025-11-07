package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.AttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentAttendanceStatusDTO;
import com.thuyloiuni.teaching_schedule_api.dto.TakeAttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Attendance;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.Student;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.AttendanceMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AttendanceRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.repository.StudentRepository;
import com.thuyloiuni.teaching_schedule_api.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public List<AttendanceDTO> takeAttendance(TakeAttendanceDTO takeAttendanceDTO) {
        Schedule schedule = scheduleRepository.findById(takeAttendanceDTO.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + takeAttendanceDTO.getScheduleId()));

        List<Attendance> savedAttendances = new ArrayList<>();

        for (StudentAttendanceStatusDTO statusDTO : takeAttendanceDTO.getAttendances()) {
            Student student = studentRepository.findById(statusDTO.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + statusDTO.getStudentId()));

            // This logic can be enhanced to update existing records if needed, but for now, it creates new ones.
            Attendance newAttendance = new Attendance();
            newAttendance.setSchedule(schedule);
            newAttendance.setStudent(student);
            newAttendance.setIsPresent(statusDTO.getIsPresent()); // Corrected from setPresent

            savedAttendances.add(attendanceRepository.save(newAttendance));
        }

        return savedAttendances.stream()
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendancesByScheduleId(Integer scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + scheduleId);
        }
        return attendanceRepository.findAll().stream()
                .filter(attendance -> attendance.getSchedule().getSessionId().equals(scheduleId))
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
    }
}
