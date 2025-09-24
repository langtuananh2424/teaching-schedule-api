package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;

@Repository

public interface StudentClassRepository extends JpaRepository<StudentClass, Integer> {
    Optional<StudentClass> findByClassCode(String classCode);

    List<StudentClass> findBySemester(String semester);
}