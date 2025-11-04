package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.MakeupSessionMapper;
import com.thuyloiuni.teaching_schedule_api.repository.MakeupSessionRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.security.CustomUserDetails;
import com.thuyloiuni.teaching_schedule_api.service.MakeupSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        User currentUser = getCurrentUser();
        List<MakeupSession> sessions;

        if (currentUser.getRole() == RoleType.MANAGER) {
            Lecturer managerLecturerInfo = currentUser.getLecturer();
            if (managerLecturerInfo == null) {
                throw new IllegalStateException("A user with MANAGER role must have an associated lecturer profile.");
            }
            Department managerDepartment = managerLecturerInfo.getDepartment();
            sessions = makeupSessionRepository.findAll().stream()
                    .filter(session -> session.getAbsentSchedule().getAssignment().getLecturer().getDepartment().equals(managerDepartment))
                    .collect(Collectors.toList());
        } else { // ADMIN
            sessions = makeupSessionRepository.findAll();
        }

        return sessions.stream()
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
        newSession.setManagerApproval(ApprovalStatus.PENDING);
        newSession.setAcademicAffairsApproval(ApprovalStatus.PENDING);

        MakeupSession savedSession = makeupSessionRepository.save(newSession);
        MakeupSessionDTO createdDto = mapToDtoWithDetails(savedSession);

        messagingTemplate.convertAndSend("/topic/new-makeup-request", createdDto);

        return createdDto;
    }

    @Override
    @Transactional
    public MakeupSessionDTO updateManagerApproval(Integer id, UpdateApprovalStatusDTO statusDto) {
        User managerUser = getCurrentUser();
        Lecturer managerLecturerInfo = managerUser.getLecturer();

        if (managerLecturerInfo == null) {
            throw new IllegalStateException("A user with MANAGER role must have an associated lecturer profile to approve requests.");
        }

        MakeupSession session = findSessionById(id);

        Department requestDepartment = session.getAbsentSchedule().getAssignment().getLecturer().getDepartment();
        if (!managerLecturerInfo.getDepartment().equals(requestDepartment)) {
            throw new AccessDeniedException("Manager can only approve requests from their own department.");
        }

        session.setManagerApproval(statusDto.getStatus());
        return updateAndNotify(session);
    }

    @Override
    @Transactional
    public MakeupSessionDTO updateAcademicAffairsApproval(Integer id, UpdateApprovalStatusDTO statusDto) {
        MakeupSession session = findSessionById(id);
        session.setAcademicAffairsApproval(statusDto.getStatus());
        return updateAndNotify(session);
    }

    private MakeupSession findSessionById(Integer id) {
        return makeupSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Makeup session not found with ID: " + id));
    }

    private MakeupSessionDTO updateAndNotify(MakeupSession session) {
        MakeupSession updatedSession = makeupSessionRepository.save(session);

        if (session.getManagerApproval() == ApprovalStatus.APPROVED && session.getAcademicAffairsApproval() == ApprovalStatus.APPROVED) {
            createScheduleForMakeupSession(session);
        }

        MakeupSessionDTO updatedDto = mapToDtoWithDetails(updatedSession);

        if (session.getAbsentSchedule() != null && session.getAbsentSchedule().getAssignment() != null && session.getAbsentSchedule().getAssignment().getLecturer() != null && session.getAbsentSchedule().getAssignment().getLecturer().getUser() != null) {
            Long userId = session.getAbsentSchedule().getAssignment().getLecturer().getUser().getUserId();
            messagingTemplate.convertAndSend("/topic/user/" + userId, updatedDto);
        }

        return updatedDto;
    }

    private void createScheduleForMakeupSession(MakeupSession makeupSession) {
        Schedule absentSchedule = makeupSession.getAbsentSchedule();

        Schedule newSchedule = new Schedule();
        newSchedule.setAssignment(absentSchedule.getAssignment());
        newSchedule.setSessionDate(makeupSession.getMakeupDate());
        newSchedule.setStartPeriod(makeupSession.getMakeupStartPeriod());
        newSchedule.setEndPeriod(makeupSession.getMakeupEndPeriod());
        newSchedule.setClassroom(makeupSession.getMakeupClassroom());
        newSchedule.setLessonOrder(absentSchedule.getLessonOrder());
        newSchedule.setStatus(ScheduleStatus.NOT_TAUGHT);
        newSchedule.setNotes("Buổi dạy bù cho lịch nghỉ có ID: " + absentSchedule.getSessionId());
        scheduleRepository.save(newSchedule);

        absentSchedule.setStatus(ScheduleStatus.TAUGHT);
        scheduleRepository.save(absentSchedule);
    }

    private MakeupSessionDTO mapToDtoWithDetails(MakeupSession session) {
        return makeupSessionMapper.toDto(session);
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
