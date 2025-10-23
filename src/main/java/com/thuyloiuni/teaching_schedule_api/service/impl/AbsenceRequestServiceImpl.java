package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ApproveAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
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

import java.time.LocalDateTime;
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

    // ... các phương thức khác giữ nguyên ...
    @Override
    @Transactional(readOnly = true)
    public List<AbsenceRequestDTO> getAllRequests() {
        List<AbsenceRequest> requests = absenceRequestRepository.findAll();
        return requests.stream()
                .map(this::mapToDtoWithMakeupInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AbsenceRequestDTO getRequestById(Integer requestId) {
        AbsenceRequest request = absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn xin nghỉ với ID: " + requestId));
        return mapToDtoWithMakeupInfo(request);
    }

    @Override
    @Transactional
    public AbsenceRequestDTO createRequest(CreateAbsenceRequestDTO createDto) {
        // 1. Tìm các đối tượng liên quan
        Schedule schedule = scheduleRepository.findById(createDto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + createDto.getSessionId()));

        Lecturer lecturer = lecturerRepository.findById(createDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + createDto.getLecturerId()));

        // 2. Tạo và lưu đơn xin nghỉ
        AbsenceRequest newRequest = new AbsenceRequest();
        newRequest.setSchedule(schedule);
        newRequest.setLecturer(lecturer);
        newRequest.setReason(createDto.getReason());
        newRequest.setApprovalStatus(ApprovalStatus.PENDING);
        newRequest.setCreatedAt(LocalDateTime.now());
        AbsenceRequest savedRequest = absenceRequestRepository.save(newRequest);

        // 3. Nếu có thông tin dạy bù, tạo và lưu MakeupSession
        if (createDto.getMakeupDate() != null && createDto.getMakeupStartPeriod() != null) {
            MakeupSession makeupSession = new MakeupSession();
            makeupSession.setAbsentRequest(schedule); // Liên kết với buổi học gốc
            makeupSession.setMakeupDate(createDto.getMakeupDate());
            makeupSession.setMakeupStartPeriod(createDto.getMakeupStartPeriod());
            makeupSession.setMakeupEndPeriod(createDto.getMakeupEndPeriod());
            makeupSession.setMakeupClassroom(createDto.getMakeupClassroom());
            makeupSession.setApprovalStatus(ApprovalStatus.PENDING); // Trạng thái của buổi dạy bù cũng là PENDING
            // makeupSession.setCreatedAt(LocalDateTime.now()); // Nếu bạn có trường createdAt trong MakeupSession
            makeupSessionRepository.save(makeupSession);
        }

        // 4. Chuẩn bị DTO trả về (đã bao gồm thông tin dạy bù nếu có)
        AbsenceRequestDTO createdDto = mapToDtoWithMakeupInfo(savedRequest);

        // 5. Gửi thông báo WebSocket
        messagingTemplate.convertAndSend("/topic/new-request", createdDto);

        return createdDto;
    }

    @Override
    @Transactional
    public AbsenceRequestDTO approveRequest(Integer requestId, ApproveAbsenceRequestDTO approveDto) {
        AbsenceRequest request = absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn xin nghỉ với ID: " + requestId));

        Lecturer approver = lecturerRepository.findById(approveDto.getApproverId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người duyệt với ID: " + approveDto.getApproverId()));

        request.setApprovalStatus(approveDto.getNewStatus());
        request.setApprover(approver);

        AbsenceRequest updatedRequest = absenceRequestRepository.save(request);
        AbsenceRequestDTO updatedDto = mapToDtoWithMakeupInfo(updatedRequest);

        Integer lecturerId = request.getLecturer().getLecturerId();
        String destination = "/topic/lecturer/" + lecturerId;
        messagingTemplate.convertAndSend(destination, updatedDto);

        return updatedDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbsenceRequestDTO> getRequestsByStatus(ApprovalStatus status) {
        List<AbsenceRequest> requests = absenceRequestRepository.findByApprovalStatus(status);
        return requests.stream()
                .map(this::mapToDtoWithMakeupInfo)
                .collect(Collectors.toList());
    }

    private AbsenceRequestDTO mapToDtoWithMakeupInfo(AbsenceRequest request) {
        AbsenceRequestDTO dto = absenceRequestMapper.toDto(request);

        Schedule originalSchedule = request.getSchedule();
        if (originalSchedule == null) {
            return dto;
        }

        Optional<MakeupSession> makeupSessionOpt = makeupSessionRepository.findByAbsentRequest(originalSchedule);

        makeupSessionOpt.ifPresent(makeupSession -> {
            // dto.setMakeupCreatedAt(makeupSession.getCreatedAt()); // Nếu có trường này
            if (makeupSession.getMakeupDate() != null) {
                dto.setMakeupDate(makeupSession.getMakeupDate().toLocalDate());
            }
            dto.setMakeupStartPeriod(makeupSession.getMakeupStartPeriod());
            dto.setMakeupEndPeriod(makeupSession.getMakeupEndPeriod());
            dto.setMakeupClassroom(makeupSession.getMakeupClassroom());
        });

        return dto;
    }
}
