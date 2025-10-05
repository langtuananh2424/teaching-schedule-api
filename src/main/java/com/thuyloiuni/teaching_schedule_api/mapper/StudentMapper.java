package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.CreateStudentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    // Ánh xạ từ đối tượng lồng nhau "studentClass"
    @Mapping(source = "studentClass.classId", target = "classId")
    @Mapping(source = "studentClass.className", target = "className")
    StudentDTO toDto(Student student);

    List<StudentDTO> toDtoList(List<Student> students);

    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "studentClass", ignore = true)
    @Mapping(target = "attendance", ignore = true)
    Student fromCreateDtoToEntity(CreateStudentDTO createStudentDTO);
}
