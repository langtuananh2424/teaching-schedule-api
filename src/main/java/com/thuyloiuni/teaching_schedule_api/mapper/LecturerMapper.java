package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    LecturerDTO toDto(Lecturer lecturer);

    @Mapping(target = "department", ignore = true)
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "lecturerId", ignore = true) // id is auto-generated
    Lecturer toEntity(LecturerDTO lecturerDTO);

    @Mapping(target = "department", ignore = true) // Will be set in service
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "password", ignore = true) // Will be encoded in service
    @Mapping(target = "lecturerId", ignore = true)
    Lecturer fromCreateDtoToEntity(CreateLecturerRequestDTO createDto);

    List<LecturerDTO> toDtoList(List<Lecturer> lecturers);
}
