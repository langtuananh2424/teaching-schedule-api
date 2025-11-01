package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentClassMapper {

    @Mapping(source = "classId", target = "classId")
    StudentClassDTO toDto(StudentClass studentClass);

    @Mapping(source = "classId", target = "classId")
    StudentClass toEntity(StudentClassDTO studentClassDTO);

    List<StudentClassDTO> toDtoList(List<StudentClass> studentClasses);
}
