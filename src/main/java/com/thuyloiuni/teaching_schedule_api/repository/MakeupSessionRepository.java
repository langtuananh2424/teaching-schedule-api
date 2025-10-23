package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MakeupSessionRepository extends JpaRepository<MakeupSession, Integer> {

    /**
     * Tìm một buổi dạy bù dựa trên buổi học gốc đã nghỉ.
     * Tên phương thức được tự động sinh ra từ tên trường 'absentRequest' trong entity MakeupSession.
     */
    Optional<MakeupSession> findByAbsentRequest(Schedule absentRequest);

    Optional<MakeupSession> findByMakeupSessionId(Integer makeupSessionId);

    List<MakeupSession> findByApprover_LecturerId(Integer approverId);

    List<MakeupSession> findByApprovalStatus(ApprovalStatus status);
}
