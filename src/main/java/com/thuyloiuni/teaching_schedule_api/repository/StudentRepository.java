package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.Student;

@Repository

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByStudentCode(String studentCode);

    List<Student> findByClass_ClassId(Integer classId);

    List<Student> findByFullNameContainingIgnoreCase(String nameFragment);
}
