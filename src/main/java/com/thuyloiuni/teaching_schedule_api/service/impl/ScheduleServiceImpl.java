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
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AssignmentRepository assignmentRepository;
    private final ScheduleMapper scheduleMapper;
    private final SimpMessageSendingOperations messagingTemplate; // Added for WebSocket

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
        updateScheduleFromDto(newSchedule, createDto); // Use helper method

        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        return scheduleMapper.toDto(savedSchedule);
    }

    @Override
    @Transactional
    public ScheduleDTO updateSchedule(Integer id, CreateScheduleDTO updateDto) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + id));

        if (!existingSchedule.getAssignment().getAssignmentId().equals(updateDto.getAssignmentId())) {
            Assignment assignment = assignmentRepository.findById(updateDto.getAssignmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + updateDto.getAssignmentId()));
            existingSchedule.setAssignment(assignment);
        }

        updateScheduleFromDto(existingSchedule, updateDto);

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);
        ScheduleDTO updatedDto = scheduleMapper.toDto(updatedSchedule);

        // --- WebSocket Integration ---
        // Send a notification to the lecturer of this schedule
        Integer lecturerId = updatedSchedule.getAssignment().getLecturer().getLecturerId();
        String destination = "/topic/lecturer/" + lecturerId;
        messagingTemplate.convertAndSend(destination, updatedDto);
        // --- End WebSocket Integration ---

        return updatedDto;
    }

    @Override
    @Transactional
    public void deleteSchedule(Integer id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + id);
        }
        scheduleRepository.deleteById(id);
    }

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
