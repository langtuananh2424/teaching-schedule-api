package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.CreateScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Assignment;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.ScheduleMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AssignmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AssignmentRepository assignmentRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleMapper.toDtoList(scheduleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleDTO getScheduleById(Integer id) {
        return scheduleRepository.findById(id)
                .map(scheduleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getSchedulesByAssignment(Integer assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Không tìm thấy phân công với ID: " + assignmentId);
        }
        return scheduleMapper.toDtoList(scheduleRepository.findByAssignment_AssignmentId(assignmentId));
    }

    @Override
    @Transactional
    public ScheduleDTO createSchedule(CreateScheduleDTO createDto) {
        Assignment assignment = assignmentRepository.findById(createDto.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + createDto.getAssignmentId()));

        Schedule newSchedule = new Schedule();
        newSchedule.setAssignment(assignment);
        updateScheduleFromDto(newSchedule, createDto); // Sử dụng helper method để gán các trường chung

        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        return scheduleMapper.toDto(savedSchedule);
    }

    @Override
    @Transactional
    public ScheduleDTO updateSchedule(Integer id, CreateScheduleDTO updateDto) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + id));

        // Kiểm tra xem phân công có thay đổi không
        if (!existingSchedule.getAssignment().getAssignmentId().equals(updateDto.getAssignmentId())) {
            Assignment assignment = assignmentRepository.findById(updateDto.getAssignmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + updateDto.getAssignmentId()));
            existingSchedule.setAssignment(assignment);
        }

        updateScheduleFromDto(existingSchedule, updateDto); // Sử dụng helper method

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);
        return scheduleMapper.toDto(updatedSchedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Integer id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + id);
        }
        // Có thể thêm logic kiểm tra ràng buộc (ví dụ: không cho xóa nếu đã có điểm danh)
        scheduleRepository.deleteById(id);
    }

    /**
     * Helper method để gán các thuộc tính từ DTO vào Entity, tránh lặp code.
     */
    private void updateScheduleFromDto(Schedule schedule, CreateScheduleDTO dto) {
        schedule.setSessionDate(dto.getSessionDate());
        schedule.setLessonOrder(dto.getLessonOrder());
        schedule.setStartPeriod(dto.getStartPeriod());
        schedule.setEndPeriod(dto.getEndPeriod());
        schedule.setClassroom(dto.getClassroom());
        schedule.setContent(dto.getContent());
        schedule.setNotes(dto.getNotes());
        schedule.setStatus(dto.getStatus());
    }
}
