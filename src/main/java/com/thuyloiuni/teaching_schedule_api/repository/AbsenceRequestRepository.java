package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;

@Repository

public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, Integer> {

    List<AbsenceRequest> findBySchedule_SessionId(Integer sessionId);

    List<AbsenceRequest> findByApprovalStatus(ApprovalStatus status);

    List<AbsenceRequest> findByApprover_LecturerId(Integer approverId);

}