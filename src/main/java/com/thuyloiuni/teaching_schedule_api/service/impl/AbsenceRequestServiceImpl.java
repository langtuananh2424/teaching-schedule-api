package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ApproveAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.AbsenceRequestMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AbsenceRequestRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.service.AbsenceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbsenceRequestServiceImpl implements AbsenceRequestService {

    private final AbsenceRequestRepository absenceRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final LecturerRepository lecturerRepository;
    private final AbsenceRequestMapper absenceRequestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AbsenceRequestDTO> getAllRequests() {
        return absenceRequestMapper.toDtoList(absenceRequestRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public AbsenceRequestDTO getRequestById(Integer requestId) {
        return absenceRequestRepository.findById(requestId)
                .map(absenceRequestMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn xin nghỉ với ID: " + requestId));
    }

    @Override
    @Transactional
    public AbsenceRequestDTO createRequest(CreateAbsenceRequestDTO createDto) {
        // Tìm các đối tượng liên quan
        Schedule schedule = scheduleRepository.findById(createDto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + createDto.getSessionId()));

        Lecturer lecturer = lecturerRepository.findById(createDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + createDto.getLecturerId()));

        // Tạo đối tượng AbsenceRequest mới
        AbsenceRequest newRequest = new AbsenceRequest();
        newRequest.setSchedule(schedule);
        newRequest.setLecturer(lecturer);
        newRequest.setReason(createDto.getReason());
        newRequest.setApprovalStatus(ApprovalStatus.PENDING); // Mặc định là chờ duyệt
        newRequest.setCreatedAt(LocalDateTime.now());
        // approver lúc này là null

        AbsenceRequest savedRequest = absenceRequestRepository.save(newRequest);
        return absenceRequestMapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public AbsenceRequestDTO approveRequest(Integer requestId, ApproveAbsenceRequestDTO approveDto) {
        // Tìm đơn xin nghỉ
        AbsenceRequest request = absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn xin nghỉ với ID: " + requestId));

        // Tìm người duyệt
        Lecturer approver = lecturerRepository.findById(approveDto.getApproverId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người duyệt với ID: " + approveDto.getApproverId()));

        // Cập nhật trạng thái
        request.setApprovalStatus(approveDto.getNewStatus());
        request.setApprover(approver);

        AbsenceRequest updatedRequest = absenceRequestRepository.save(request);
        return absenceRequestMapper.toDto(updatedRequest);
    }
}
