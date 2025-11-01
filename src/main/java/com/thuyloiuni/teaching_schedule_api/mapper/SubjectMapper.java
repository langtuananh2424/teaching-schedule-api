package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    // MapStruct will automatically map fields with the same name:
    // subjectCode, subjectName, credits, theoryPeriods, practicePeriods
    @Mapping(source = "subjectId", target = "id")
    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    SubjectDTO toDto(Subject subject);

    @Mapping(target = "subjectId", source = "id")
    @Mapping(target = "department", ignore = true) // Department will be set in the service layer
    @Mapping(target = "assignments", ignore = true)
        // Assignments are a relationship, not mapped from DTO
    Subject toEntity(SubjectDTO subjectDTO);

    List<SubjectDTO> toDtoList(List<Subject> subjects);
}
