package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByAssignment_AssignmentId(Integer assignmentId);
    List<Schedule> findByStatus(ScheduleStatus status);
    List<Schedule> findByAssignment_Lecturer_Email(String email);
}
