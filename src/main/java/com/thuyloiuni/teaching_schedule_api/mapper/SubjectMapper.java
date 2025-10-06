package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Đảm bảo Spring có thể inject mapper này vào service
public interface SubjectMapper {

    SubjectMapper INSTANCE = Mappers.getMapper(SubjectMapper.class);

    /**
     * Chuyển đổi từ Subject (Entity) sang SubjectDTO.
     * @param subject Đối tượng Entity.
     * @return Đối tượng DTO.
     */
    @Mapping(source = "subjectId", target = "id")
    @Mapping(source = "subjectCode", target = "subjectCode")
    @Mapping(source = "subjectName", target = "subjectName")
    @Mapping(source = "credits", target = "credits")
    SubjectDTO toDto(Subject subject);

    /**
     * Chuyển đổi từ SubjectDTO sang Subject (Entity).
     * @param subjectDTO Đối tượng DTO.
     * @return Đối tượng Entity.
     */
    @Mapping(target = "subjectId", source = "id")
    @Mapping(target = "subjectCode", source = "subjectCode")
    @Mapping(target = "subjectName", source = "subjectName")
    @Mapping(target = "credits", source = "credits")
    // Không cần map các mối quan hệ phức tạp ở đây vì Subject là một entity đơn giản.
    Subject toEntity(SubjectDTO subjectDTO);

    /**
     * Chuyển đổi một danh sách Subject (Entity) sang danh sách SubjectDTO.
     * MapStruct sẽ tự động sử dụng phương thức toDto ở trên cho mỗi phần tử.
     * @param subjects Danh sách Entity.
     * @return Danh sách DTO.
     */
    List<SubjectDTO> toDtoList(List<Subject> subjects);
}
