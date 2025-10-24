package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    SubjectMapper INSTANCE = Mappers.getMapper(SubjectMapper.class);

    /**
     * Chuyển đổi từ Subject (Entity) sang SubjectDTO.
     */
    @Mapping(source = "subjectId", target = "id")
    @Mapping(source = "subjectCode", target = "subjectCode")
    @Mapping(source = "subjectName", target = "subjectName")
    @Mapping(source = "credits", target = "credits")
    // Ánh xạ thông tin từ đối tượng Department lồng trong Subject
    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    SubjectDTO toDto(Subject subject);

    /**
     * Chuyển đổi từ SubjectDTO sang Subject (Entity).
     * Việc set đối tượng Department sẽ được xử lý ở tầng Service.
     */
    @Mapping(target = "subjectId", source = "id")
    @Mapping(target = "subjectCode", source = "subjectCode")
    @Mapping(target = "subjectName", source = "subjectName")
    @Mapping(target = "credits", source = "credits")
    @Mapping(target = "department", ignore = true) // Bỏ qua việc ánh xạ Department ở đây
    Subject toEntity(SubjectDTO subjectDTO);

    /**
     * Chuyển đổi một danh sách Subject (Entity) sang danh sách SubjectDTO.
     */
    List<SubjectDTO> toDtoList(List<Subject> subjects);
}
