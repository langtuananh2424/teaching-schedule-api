package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findBySchedule_SessionId(Integer sessionId);

    List<Attendance> findByStudent_StudentId(Integer studentId);

    Optional<Attendance> findBySchedule_SessionIdAndStudent_StudentId(Integer sessionId, Integer studentId);

    List<Attendance> findBySchedule_SessionIdAndIsPresent(Integer sessionId, Boolean isPresent);
}