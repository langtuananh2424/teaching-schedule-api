package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    // MapStruct handles lecturerCode, fullName, email, role automatically
    @Mapping(source = "lecturerId", target = "lecturerId")
    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    LecturerDTO toDto(Lecturer lecturer);

    @Mapping(source = "lecturerId", target = "lecturerId")
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "assignments", ignore = true)
    Lecturer toEntity(LecturerDTO lecturerDTO);

    List<LecturerDTO> toDtoList(List<Lecturer> lecturers);
}
