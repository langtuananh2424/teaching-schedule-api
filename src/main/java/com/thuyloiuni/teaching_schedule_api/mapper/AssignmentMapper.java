package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.AssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class, StudentClassMapper.class, LecturerMapper.class, SemesterMapper.class})
public interface AssignmentMapper {

    AssignmentDTO toDto(Assignment assignment);

    @Mapping(target = "schedules", ignore = true) // Schedules are a relationship, not mapped from DTO
    Assignment toEntity(AssignmentDTO assignmentDTO);

    List<AssignmentDTO> toDtoList(List<Assignment> assignments);
}
