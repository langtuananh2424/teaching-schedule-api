package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {

    Optional<Lecturer> findByLecturerCode(String lecturerCode);

    boolean existsByLecturerCode(String lecturerCode);

    Optional<Lecturer> findByUser_Email(String email);

    List<Lecturer> findByDepartment_DepartmentId(Integer departmentId);

    List<Lecturer> findByUser_Role(RoleType role);

}
