package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, Integer> {

    // This method can be used to find a request associated with a specific schedule session.
    List<AbsenceRequest> findBySchedule_SessionId(Integer sessionId);

    // The methods findByApprovalStatus and findByApprover_LecturerId have been removed
    // as they are no longer compatible with the new two-level approval logic.
    // If you need to find requests by status, you should create specific queries
    // that check the departmentApproval and ctsvApproval fields, for example:
    // List<AbsenceRequest> findByDepartmentApprovalOrCtsvApproval(ApprovalStatus deptStatus, ApprovalStatus ctsvStatus);

}
