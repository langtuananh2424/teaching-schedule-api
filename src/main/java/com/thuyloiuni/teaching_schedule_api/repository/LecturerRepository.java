package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;

@Repository

public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {
    Optional<Lecturer> findByLecturerCode(String lecturerCode);

    Optional<Lecturer> findByEmail(String email);

    List<Lecturer> findByDepartment_DepartmentId(Integer departmentId);
    List<Lecturer> findByRole(RoleType role);
}