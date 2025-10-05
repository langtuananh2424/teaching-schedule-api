package com.thuyloiuni.teaching_schedule_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;

@Mapper(componentModel="spring")
public interface LecturerMapper {
    LecturerMapper INSTANCE = Mappers.getMapper(LecturerMapper.class);

    @Mapping(source= "lecturerId", target= "id")
    @Mapping(source = "lecturerCode", target = "lecturerCode")
    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    LecturerDTO lecturerToLecturerDTO(Lecturer lecturer);

    @Mapping(source = "departmentId", target = "department.departmentId")
    @Mapping(target= "lecturerId", source= "id")
    // Không map departmentName từ DTO về Entity vì nó chỉ để hiển thị
    // Không map password từ DTO về Entity
    @Mapping(target = "role", ignore= true)
    Lecturer lecturerDTOToLecturer(LecturerDTO lecturerDTO);

    // Helper method to convert Enum to String
    @Named("roleToString")
    default String roleToString(Enum<?> role) {
        return role == null ? null : role.name();
    }

    default Lecturer updateDepartmentFromDTO(Lecturer lecturer, LecturerDTO lecturerDTO, Department department) {
        if (lecturerDTO.getDepartmentId() != null && department != null) {
            lecturer.setDepartment(department);
        }
        return lecturer;
    }
}
