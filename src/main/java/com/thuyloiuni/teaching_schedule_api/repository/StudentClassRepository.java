// src/main/java/com/thuyloiuni/teaching_schedule_api/repository/StudentClassRepository.java
package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Integer> {
    Optional<StudentClass> findByClassCode(String classCode);
    boolean existsByClassCode(String classCode);
}

