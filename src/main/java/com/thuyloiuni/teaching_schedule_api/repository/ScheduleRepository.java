package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByAssignment_AssignmentId(Integer assignmentId);

    List<Schedule> findByStatus(ScheduleStatus status);

    List<Schedule> findByAssignment_Lecturer_User_Email(String email);

    @Query("SELECT COALESCE(SUM(s.endPeriod - s.startPeriod + 1), 0) FROM Schedule s WHERE s.assignment.id = :assignmentId AND s.status = 'TAUGHT'")
    int sumTaughtPeriodsByAssignmentId(@Param("assignmentId") Integer assignmentId);
}
