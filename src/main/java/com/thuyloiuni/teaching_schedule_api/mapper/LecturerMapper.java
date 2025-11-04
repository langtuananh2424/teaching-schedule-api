package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    @Mappings({
        @Mapping(source = "lecturerId", target = "lecturerId"),
        @Mapping(source = "fullName", target = "fullName"),
        @Mapping(source = "lecturerCode", target = "lecturerCode"),
        @Mapping(source = "department.departmentId", target = "departmentId"),
        @Mapping(source = "department.departmentName", target = "departmentName"),
        // Map fields from the associated User entity
        @Mapping(source = "user.email", target = "email"),
        @Mapping(source = "user.role", target = "role")
    })
    LecturerDTO toDto(Lecturer lecturer);

    @Mappings({
        @Mapping(target = "lecturerId", ignore = true),
        @Mapping(target = "department", ignore = true),
        @Mapping(target = "user", ignore = true), // User will be set in the service layer
        @Mapping(target = "assignments", ignore = true)
    })
    Lecturer toEntity(LecturerDTO lecturerDTO);
    
    // This method is no longer suitable because creating a Lecturer now requires creating a User first.
    // The logic is now handled in the LecturerServiceImpl.
    // We keep it here but it should ideally be removed or marked as deprecated.
    @Mappings({
        @Mapping(target = "lecturerId", ignore = true),
        @Mapping(target = "department", ignore = true),
        @Mapping(target = "user", ignore = true),
        @Mapping(target = "assignments", ignore = true)
    })
    Lecturer fromCreateDtoToEntity(CreateLecturerRequestDTO createDto);

    List<LecturerDTO> toDtoList(List<Lecturer> lecturers);
}
