package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    @Mapping(source = "lecturerId", target = "id")
    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    LecturerDTO toDto(Lecturer lecturer);

    List<LecturerDTO> toDtoList(List<Lecturer> lecturers);

    /**
     * Chuyển đổi từ CreateLecturerRequestDTO sang Lecturer.
     * Bỏ qua các trường phức tạp sẽ được xử lý thủ công trong service.
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "lecturerId", ignore = true)
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "role", ignore = true) // [SỬA LỖI] Yêu cầu Mapper bỏ qua trường này
    Lecturer fromCreateDtoToEntity(CreateLecturerRequestDTO createDto);
}
