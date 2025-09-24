package com.thuyloiuni.teaching_schedule_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;

@Repository

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByAssignment_AssignmentId(Integer assignmentId);

    List<Schedule> findByAssignment_Lecturer_LecturerId(Integer lecturerId);

    List<Schedule> findBySessionDate(LocalDate sessionDate);

    List<Schedule> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);

    List<Schedule> findByAssignment_Lecturer_LecturerIdAndSessionDateBetween(Integer lecturerId, LocalDate startDate, LocalDate endDate);

    List<Schedule> findByStatus(ScheduleStatus status);

    // @Query("SELECT s FROM Schedule s WHERE s.assignment.lecturer.lecturerId = :lecturerId " +
    //        "AND s.sessionDate = :date " +
    //        "AND s.startPeriod = :startPeriod")
    // Optional<Schedule> findByLecturerAndDateAndStartPeriod(
    //         @Param("lecturerId") Integer lecturerId,
    //         @Param("date") LocalDate date,
    //         @Param("startPeriod") Integer startPeriod
    // );

    List<Schedule> findByAssignment_StudentClass_ClassIdAndSessionDate(Integer classId, LocalDate sessionDate);
}