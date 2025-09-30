package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.Assignment;

@Repository

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByLecturer_LecturerId(Integer lecturerId);

    List<Assignment> findBySubject_SubjectId(Integer subjectId);

    List<Assignment> findByStudentClass_ClassId(Integer classId);

    List<Assignment> findByLecturer_LecturerIdAndSubject_SubjectId(Integer lecturerId, Integer subjectId);
}