package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;
import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.AbsenceRequestMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AbsenceRequestRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.MakeupSessionRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.service.AbsenceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbsenceRequestServiceImpl implements AbsenceRequestService {

    private final AbsenceRequestRepository absenceRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final LecturerRepository lecturerRepository;
    private final MakeupSessionRepository makeupSessionRepository;
    private final AbsenceRequestMapper absenceRequestMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<AbsenceRequestDTO> getAllRequests() {
        return absenceRequestRepository.findAll().stream()
                .map(this::mapToDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AbsenceRequestDTO getRequestById(Integer requestId) {
        AbsenceRequest request = findRequestById(requestId);
        return mapToDtoWithDetails(request);
    }

    @Override
    @Transactional
    public AbsenceRequestDTO createRequest(CreateAbsenceRequestDTO createDto) {
        Schedule schedule = scheduleRepository.findById(createDto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with ID: " + createDto.getSessionId()));

        Lecturer lecturer = lecturerRepository.findById(createDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with ID: " + createDto.getLecturerId()));

        AbsenceRequest newRequest = new AbsenceRequest();
        newRequest.setSchedule(schedule);
        newRequest.setLecturer(lecturer);
        newRequest.setReason(createDto.getReason());
        // Default statuses are set by @PrePersist in the entity

        AbsenceRequest savedRequest = absenceRequestRepository.save(newRequest);

        // Create associated makeup session if details are provided
        if (createDto.getMakeupDate() != null) {
            MakeupSession makeupSession = new MakeupSession();
            makeupSession.setAbsentSchedule(schedule);
            makeupSession.setMakeupDate(createDto.getMakeupDate());
            makeupSession.setMakeupStartPeriod(createDto.getMakeupStartPeriod());
            makeupSession.setMakeupEndPeriod(createDto.getMakeupEndPeriod());
            makeupSession.setMakeupClassroom(createDto.getMakeupClassroom());
            // Default statuses are also set by @PrePersist
            makeupSessionRepository.save(makeupSession);
        }

        AbsenceRequestDTO createdDto = mapToDtoWithDetails(savedRequest);
        messagingTemplate.convertAndSend("/topic/new-request", createdDto);

        return createdDto;
    }

    @Override
    @Transactional
    public AbsenceRequestDTO updateDepartmentApproval(Integer requestId, UpdateApprovalStatusDTO statusDto) {
        AbsenceRequest request = findRequestById(requestId);
        request.setDepartmentApproval(statusDto.getStatus());
        return updateAndNotify(request);
    }

    @Override
    @Transactional
    public AbsenceRequestDTO updateCtsvApproval(Integer requestId, UpdateApprovalStatusDTO statusDto) {
        AbsenceRequest request = findRequestById(requestId);
        request.setCtsvApproval(statusDto.getStatus());
        return updateAndNotify(request);
    }

    // Helper methods

    private AbsenceRequest findRequestById(Integer requestId) {
        return absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Absence request not found with ID: " + requestId));
    }

    private AbsenceRequestDTO updateAndNotify(AbsenceRequest request) {
        AbsenceRequest updatedRequest = absenceRequestRepository.save(request);
        AbsenceRequestDTO updatedDto = mapToDtoWithDetails(updatedRequest);

        // Notify the specific lecturer about the status change
        Integer lecturerId = request.getLecturer().getLecturerId();
        messagingTemplate.convertAndSend("/topic/lecturer/" + lecturerId, updatedDto);

        return updatedDto;
    }

    private AbsenceRequestDTO mapToDtoWithDetails(AbsenceRequest request) {
        AbsenceRequestDTO dto = absenceRequestMapper.toDto(request);

        // Now we explicitly map the new status fields from the entity to the DTO
        dto.setDepartmentStatus(request.getDepartmentApproval());
        dto.setCtsvStatus(request.getCtsvApproval());

        Schedule originalSchedule = request.getSchedule();
        if (originalSchedule != null) {
            Optional<MakeupSession> makeupSessionOpt = makeupSessionRepository.findByAbsentSchedule(originalSchedule);
            makeupSessionOpt.ifPresent(makeupSession -> {
                dto.setMakeupCreatedAt(makeupSession.getCreatedAt());
                dto.setMakeupDate(makeupSession.getMakeupDate().toLocalDate());
                dto.setMakeupStartPeriod(makeupSession.getMakeupStartPeriod());
                dto.setMakeupEndPeriod(makeupSession.getMakeupEndPeriod());
                dto.setMakeupClassroom(makeupSession.getMakeupClassroom());
            });
        }

        return dto;
    }
}
