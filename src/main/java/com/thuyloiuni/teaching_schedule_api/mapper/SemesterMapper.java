package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.SemesterDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SemesterMapper {

    @Mapping(source = "id", target = "semesterId")
    SemesterDTO toDto(Semester semester);

    @Mapping(source = "semesterId", target = "id")
    @Mapping(target = "assignments", ignore = true)
    Semester toEntity(SemesterDTO semesterDTO);

    List<SemesterDTO> toDtoList(List<Semester> semesters);
}
