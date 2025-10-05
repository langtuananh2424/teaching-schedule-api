// src/main/java/com/thuyloiuni/teaching_schedule_api/mapper/DepartmentMapper.java
package com.thuyloiuni.teaching_schedule_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;

@Mapper(componentModel = "spring") // componentModel = "spring" để Spring có thể inject
public interface DepartmentMapper {

    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);
    DepartmentDTO departmentToDepartmentDTO(Department department);
     @Mapping(target = "lecturers", ignore = true) // Bỏ qua việc map lecturers từ DTO
     @Mapping(target = "subjects", ignore = true)  // Bỏ qua việc map subjects từ DTO
    Department departmentDTOToDepartment(DepartmentDTO departmentDTO);
}
