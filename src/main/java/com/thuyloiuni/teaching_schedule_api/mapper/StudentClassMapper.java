package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentClassMapper {

    StudentClassDTO toDto(StudentClass studentClass);

    StudentClass toEntity(StudentClassDTO studentClassDTO);

    List<StudentClassDTO> toDtoList(List<StudentClass> studentClasses);
}
