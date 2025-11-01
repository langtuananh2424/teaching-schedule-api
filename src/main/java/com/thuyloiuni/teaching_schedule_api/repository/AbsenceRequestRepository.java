package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, Integer> {

    List<AbsenceRequest> findBySchedule_SessionId(Integer sessionId);

    /**
     * Finds an absence request by the Schedule entity.
     * This is used to check for duplicate requests for the same session.
     */
    Optional<AbsenceRequest> findBySchedule(Schedule schedule);

}
