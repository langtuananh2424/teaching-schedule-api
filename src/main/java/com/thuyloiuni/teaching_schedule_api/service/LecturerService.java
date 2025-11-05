package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;

import java.util.List;

public interface LecturerService {

    LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO);

    LecturerDTO updateLecturer(Integer id, CreateLecturerRequestDTO lecturerRequestDTO);

    void deleteLecturer(Integer id);

    LecturerDTO getLecturerById(Integer id);

    LecturerDTO getLecturerByCode(String code);

    LecturerDTO getLecturerByEmail(String email);

    List<LecturerDTO> getAllLecturers();

    List<LecturerDTO> getLecturersByDepartmentId(Integer departmentId);

    List<LecturerDTO> getLecturersByRole(RoleType role);

}
