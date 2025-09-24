package com.thuyloiuni.teaching_schedule_api.service;

import java.util.List;
import java.util.Optional;

import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    Optional<DepartmentDTO> getDepartmentById(Integer departmentId);

    List<DepartmentDTO> getAllDepartments();

    DepartmentDTO updateDepartment(Integer departmentId, DepartmentDTO departmentDTO);

    void deleteDepartment(Integer departmentId);
    
    Optional<DepartmentDTO> getDepartmentByName(String departmentName);
}
