package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;
import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.MakeupSessionMapper;
import com.thuyloiuni.teaching_schedule_api.repository.MakeupSessionRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.service.MakeupSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MakeupSessionServiceImpl implements MakeupSessionService {

    private final MakeupSessionRepository makeupSessionRepository;
    private final ScheduleRepository scheduleRepository;
    private final MakeupSessionMapper makeupSessionMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<MakeupSessionDTO> getAllMakeupSessions() {
        return makeupSessionRepository.findAll().stream()
                .map(this::mapToDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MakeupSessionDTO getMakeupSessionById(Integer id) {
        MakeupSession session = findSessionById(id);
        return mapToDtoWithDetails(session);
    }

    @Override
    @Transactional
    public MakeupSessionDTO createMakeupSession(CreateMakeupSessionDTO createDto) {
        Schedule absentSchedule = scheduleRepository.findById(createDto.getAbsentSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Absent schedule not found with ID: " + createDto.getAbsentSessionId()));

        MakeupSession newSession = new MakeupSession();
        newSession.setAbsentSchedule(absentSchedule);
        newSession.setMakeupDate(createDto.getMakeupDate());
        newSession.setMakeupStartPeriod(createDto.getMakeupStartPeriod());
        newSession.setMakeupEndPeriod(createDto.getMakeupEndPeriod());
        newSession.setMakeupClassroom(createDto.getMakeupClassroom());
        // Default statuses are set by @PrePersist in the entity

        MakeupSession savedSession = makeupSessionRepository.save(newSession);
        MakeupSessionDTO createdDto = mapToDtoWithDetails(savedSession);

        messagingTemplate.convertAndSend("/topic/new-makeup-request", createdDto);

        return createdDto;
    }

    @Override
    @Transactional
    public MakeupSessionDTO updateDepartmentApproval(Integer id, UpdateApprovalStatusDTO statusDto) {
        MakeupSession session = findSessionById(id);
        session.setDepartmentApproval(statusDto.getStatus());
        return updateAndNotify(session);
    }

    @Override
    @Transactional
    public MakeupSessionDTO updateCtsvApproval(Integer id, UpdateApprovalStatusDTO statusDto) {
        MakeupSession session = findSessionById(id);
        session.setCtsvApproval(statusDto.getStatus());
        return updateAndNotify(session);
    }

    // Helper methods

    private MakeupSession findSessionById(Integer id) {
        return makeupSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Makeup session not found with ID: " + id));
    }

    private MakeupSessionDTO updateAndNotify(MakeupSession session) {
        MakeupSession updatedSession = makeupSessionRepository.save(session);
        MakeupSessionDTO updatedDto = mapToDtoWithDetails(updatedSession);

        // Notify the lecturer who owns the original schedule
        Integer lecturerId = session.getAbsentSchedule().getAssignment().getLecturer().getLecturerId();
        messagingTemplate.convertAndSend("/topic/lecturer/" + lecturerId, updatedDto);

        return updatedDto;
    }

    private MakeupSessionDTO mapToDtoWithDetails(MakeupSession session) {
        MakeupSessionDTO dto = makeupSessionMapper.toDto(session);
        dto.setDepartmentStatus(session.getDepartmentApproval());
        dto.setCtsvStatus(session.getCtsvApproval());
        return dto;
    }
}
