package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Attendance;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findByScheduleAndStudent(Schedule schedule, Student student);

    List<Attendance> findBySchedule_SessionId(Integer sessionId);

    @Query("SELECT att.student.id, COUNT(att) FROM Attendance att WHERE att.schedule.assignment.id = :assignmentId AND att.isPresent = true GROUP BY att.student.id")
    List<Object[]> countAttendedSessionsByStudentForAssignment(@Param("assignmentId") Integer assignmentId);
}
