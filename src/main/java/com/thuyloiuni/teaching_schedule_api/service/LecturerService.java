package com.thuyloiuni.teaching_schedule_api.service;

import java.util.List;
import java.util.Optional;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;


public interface LecturerService {
    LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO);
    Optional<LecturerDTO> getLecturerById(Integer id);
    Optional<LecturerDTO> getLecturerByEmail(String email);
    Optional<LecturerDTO> getLecturerByCode(String code);

    List<LecturerDTO> getAllLecturers();
    List<LecturerDTO> getLecturersByDepartmentId(Integer departmentId);
    List<LecturerDTO> getLecturersByRole(RoleType role);
    LecturerDTO updateLecturer(Integer id, CreateLecturerRequestDTO lecturerRequestDTO);
    void deleteLecturer(Integer id);
}
