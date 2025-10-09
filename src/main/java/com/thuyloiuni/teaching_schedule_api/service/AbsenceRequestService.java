package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ApproveAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import java.util.List;

public interface AbsenceRequestService {
    List<AbsenceRequestDTO> getAllRequests();
    AbsenceRequestDTO getRequestById(Integer requestId);
    AbsenceRequestDTO createRequest(CreateAbsenceRequestDTO createDto);
    AbsenceRequestDTO approveRequest(Integer requestId, ApproveAbsenceRequestDTO approveDto);

    List<AbsenceRequestDTO> getRequestsByStatus(ApprovalStatus status);
}
