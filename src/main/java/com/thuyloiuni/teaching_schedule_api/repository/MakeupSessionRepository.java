package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;

@Repository

public interface MakeupSessionRepository extends JpaRepository<MakeupSession, Integer> {
    Optional<MakeupSession> findByOriginalAbsentSession_SessionId(Integer absentSessionId);

    List<MakeupSession> findByApprover_LecturerId(Integer approverId);

    List<MakeupSession> findByApprovalStatus(ApprovalStatus status);
}