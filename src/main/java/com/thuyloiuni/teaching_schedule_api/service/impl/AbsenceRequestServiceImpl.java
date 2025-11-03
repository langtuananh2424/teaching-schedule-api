package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.exception.BadRequestException;
import com.thuyloiuni.teaching_schedule_api.exception.RequestConflictException;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.AbsenceRequestMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AbsenceRequestRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.MakeupSessionRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.security.CustomUserDetails;
import com.thuyloiuni.teaching_schedule_api.service.AbsenceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
        Lecturer currentUser = getCurrentUser();
        List<AbsenceRequest> requests;

        if (currentUser.getRole() == RoleType.MANAGER) {
            Department managerDepartment = currentUser.getDepartment();
            requests = absenceRequestRepository.findAll().stream()
                    .filter(request -> request.getLecturer().getDepartment().equals(managerDepartment))
                    .collect(Collectors.toList());
        } else {
            // ADMIN sees all requests
            requests = absenceRequestRepository.findAll();
        }

        return requests.stream()
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
        Lecturer currentUser = getCurrentUser();

        // Security Check: If the user is a LECTURER, they can only create requests for themselves.
        if (currentUser.getRole() == RoleType.LECTURER) {
            if (!Objects.equals(createDto.getLecturerId(), currentUser.getLecturerId())) {
                throw new AccessDeniedException("Lecturers can only create absence requests for themselves.");
            }
        }

        Schedule schedule = scheduleRepository.findById(createDto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with ID: " + createDto.getSessionId()));

        validateAbsenceRequest(schedule, createDto);

        Lecturer lecturer = lecturerRepository.findById(createDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with ID: " + createDto.getLecturerId()));

        AbsenceRequest newRequest = new AbsenceRequest();
        newRequest.setSchedule(schedule);
        newRequest.setLecturer(lecturer);
        newRequest.setReason(createDto.getReason());
        newRequest.setManagerApproval(ApprovalStatus.PENDING);
        newRequest.setAcademicAffairsApproval(ApprovalStatus.PENDING);

        AbsenceRequest savedRequest = absenceRequestRepository.save(newRequest);

        if (createDto.getMakeupDate() != null) {
            MakeupSession makeupSession = new MakeupSession();
            makeupSession.setAbsentSchedule(schedule);
            makeupSession.setMakeupDate(createDto.getMakeupDate());
            makeupSession.setMakeupStartPeriod(createDto.getMakeupStartPeriod());
            makeupSession.setMakeupEndPeriod(createDto.getMakeupEndPeriod());
            makeupSession.setMakeupClassroom(createDto.getMakeupClassroom());
            makeupSessionRepository.save(makeupSession);
        }

        AbsenceRequestDTO createdDto = mapToDtoWithDetails(savedRequest);
        messagingTemplate.convertAndSend("/topic/new-request", createdDto);

        return createdDto;
    }

    private void validateAbsenceRequest(Schedule schedule, CreateAbsenceRequestDTO createDto) {
        if (absenceRequestRepository.findBySchedule(schedule).isPresent()) {
            throw new RequestConflictException("Đơn xin nghỉ cho buổi học này đã tồn tại.");
        }
        if (createDto.getMakeupDate() != null) {
            if (makeupSessionRepository.findByAbsentSchedule(schedule).isPresent()) {
                throw new RequestConflictException("Buổi học này đã được đăng ký dạy bù từ trước.");
            }
            if (!createDto.getMakeupDate().isAfter(schedule.getSessionDate())) {
                throw new BadRequestException("Ngày dạy bù phải sau ngày của buổi học gốc.");
            }
        }
    }

    @Override
    @Transactional
    public AbsenceRequestDTO updateManagerApproval(Integer requestId, UpdateApprovalStatusDTO statusDto) {
        Lecturer manager = getCurrentUser();
        AbsenceRequest request = findRequestById(requestId);

        Department requestDepartment = request.getLecturer().getDepartment();
        if (!manager.getDepartment().equals(requestDepartment)) {
            throw new AccessDeniedException("Manager can only approve requests from their own department.");
        }

        request.setManagerApproval(statusDto.getStatus());
        return updateAndNotify(request);
    }

    @Override
    @Transactional
    public AbsenceRequestDTO updateAcademicAffairsApproval(Integer requestId, UpdateApprovalStatusDTO statusDto) {
        AbsenceRequest request = findRequestById(requestId);
        request.setAcademicAffairsApproval(statusDto.getStatus());
        return updateAndNotify(request);
    }

    private AbsenceRequest findRequestById(Integer requestId) {
        return absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Absence request not found with ID: " + requestId));
    }

    private AbsenceRequestDTO updateAndNotify(AbsenceRequest request) {
        AbsenceRequest updatedRequest = absenceRequestRepository.save(request);
        AbsenceRequestDTO updatedDto = mapToDtoWithDetails(updatedRequest);

        Integer lecturerId = request.getLecturer().getLecturerId();
        messagingTemplate.convertAndSend("/topic/lecturer/" + lecturerId, updatedDto);

        return updatedDto;
    }

    private AbsenceRequestDTO mapToDtoWithDetails(AbsenceRequest request) {
        AbsenceRequestDTO dto = absenceRequestMapper.toDto(request);

        Schedule originalSchedule = request.getSchedule();
        if (originalSchedule != null) {
            makeupSessionRepository.findByAbsentSchedule(originalSchedule).ifPresent(makeupSession -> {
                dto.setMakeupCreatedAt(makeupSession.getCreatedAt());
                dto.setMakeupDate(makeupSession.getMakeupDate().toLocalDate());
                dto.setMakeupStartPeriod(makeupSession.getMakeupStartPeriod());
                dto.setMakeupEndPeriod(makeupSession.getMakeupEndPeriod());
                dto.setMakeupClassroom(makeupSession.getMakeupClassroom());
            });
        }
        return dto;
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
        // This fallback might be useful if you have other user types or in tests
        if (principal instanceof Lecturer) {
            return (Lecturer) principal;
        }
        throw new IllegalStateException("The user principal is not of an expected type.");
    }
}
