package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MakeupSessionRepository extends JpaRepository<MakeupSession, Integer> {

    /**
     * Finds a makeup session based on the original absent schedule.
     * This is crucial for checking for duplicate makeup sessions.
     */
    Optional<MakeupSession> findByAbsentSchedule(Schedule absentSchedule);

}
