package com.thuyloiuni.teaching_schedule_api.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;

@Repository

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByAssignment_AssignmentId(Integer assignmentId);

    List<Schedule> findByAssignment_Lecturer_LecturerId(Integer lecturerId);

    List<Schedule> findBySessionDate(LocalDateTime sessionDate);

    List<Schedule> findBySessionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Schedule> findByAssignment_Lecturer_LecturerIdAndSessionDateBetween(Integer lecturerId, LocalDateTime startDate, LocalDateTime endDate);

    List<Schedule> findByStatus(ScheduleStatus status);

    List<Schedule> findByAssignment_StudentClass_ClassIdAndSessionDate(Integer classId, LocalDateTime sessionDate);
}