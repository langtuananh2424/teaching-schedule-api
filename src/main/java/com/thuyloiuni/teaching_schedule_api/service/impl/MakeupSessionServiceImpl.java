package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.ApproveMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.MakeupSessionMapper;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.MakeupSessionRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.service.MakeupSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MakeupSessionServiceImpl implements MakeupSessionService {

    private final MakeupSessionRepository makeupSessionRepository;
    private final ScheduleRepository scheduleRepository;
    private final LecturerRepository lecturerRepository;
    private final MakeupSessionMapper makeupSessionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MakeupSessionDTO> getAllMakeupSessions() {
        return makeupSessionMapper.toDtoList(makeupSessionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public MakeupSessionDTO getMakeupSessionById(Integer id) {
        return makeupSessionRepository.findById(id)
                .map(makeupSessionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi dạy bù với ID: " + id));
    }

    @Override
    @Transactional
    public MakeupSessionDTO createMakeupSession(CreateMakeupSessionDTO createDto) {
        Schedule absentSchedule = scheduleRepository.findById(createDto.getAbsentSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học đã nghỉ với ID: " + createDto.getAbsentSessionId()));

        MakeupSession newSession = new MakeupSession();
        newSession.setAbsentRequest(absentSchedule);
        newSession.setMakeupDate(createDto.getMakeupDate());
        newSession.setMakeupStartPeriod(createDto.getMakeupStartPeriod());
        newSession.setMakeupEndPeriod(createDto.getMakeupEndPeriod());
        newSession.setMakeupClassroom(createDto.getMakeupClassroom());
        newSession.setApprovalStatus(ApprovalStatus.PENDING); // Mặc định là chờ duyệt

        MakeupSession savedSession = makeupSessionRepository.save(newSession);
        return makeupSessionMapper.toDto(savedSession);
    }

    @Override
    @Transactional
    public MakeupSessionDTO approveMakeupSession(Integer id, ApproveMakeupSessionDTO approveDto) {
        MakeupSession session = makeupSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi dạy bù với ID: " + id));

        Lecturer approver = lecturerRepository.findById(approveDto.getApproverId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người duyệt với ID: " + approveDto.getApproverId()));

        session.setApprovalStatus(approveDto.getNewStatus());
        session.setApprover(approver);

        MakeupSession updatedSession = makeupSessionRepository.save(session);
        return makeupSessionMapper.toDto(updatedSession);
    }
}
