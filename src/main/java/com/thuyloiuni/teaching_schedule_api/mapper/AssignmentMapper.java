package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.AssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    // Ánh xạ từ các trường của các đối tượng lồng nhau
    @Mapping(source = "subject.subjectId", target = "subjectId")
    @Mapping(source = "subject.subjectName", target = "subjectName")
    @Mapping(source = "studentClass.classId", target = "classId")
    @Mapping(source = "studentClass.className", target = "className")
    @Mapping(source = "lecturer.lecturerId", target = "lecturerId")
    @Mapping(source = "lecturer.fullName", target = "lecturerName")
    AssignmentDTO toDto(Assignment assignment);

    List<AssignmentDTO> toDtoList(List<Assignment> assignments);
}
