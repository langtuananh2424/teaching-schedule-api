package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.ApproveMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;

import java.util.List;

public interface MakeupSessionService {
    List<MakeupSessionDTO> getAllMakeupSessions();
    MakeupSessionDTO getMakeupSessionById(Integer id);
    MakeupSessionDTO createMakeupSession(CreateMakeupSessionDTO createDto);
    MakeupSessionDTO approveMakeupSession(Integer id, ApproveMakeupSessionDTO approveDto);
    List<MakeupSessionDTO> getMakeupSessionsByStatus(ApprovalStatus status);
}
