package com.thuyloiuni.teaching_schedule_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thuyloiuni.teaching_schedule_api.entity.Department;

@Repository

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByDepartName(String departName);
}
