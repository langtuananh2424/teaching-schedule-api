package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring") // Đảm bảo Spring có thể inject
public interface LecturerMapper {

    /**
     * Chuyển đổi từ Lecturer (Entity) sang LecturerDTO (dữ liệu trả về cho client).
     */
    @Mapping(source = "lecturerId", target = "id")
    @Mapping(source = "department.departmentId", target = "departmentId") // Lấy ID từ đối tượng Department
    @Mapping(source = "department.departmentName", target = "departmentName") // Lấy tên từ đối tượng Department
    LecturerDTO toDto(Lecturer lecturer);

    /**
     * Chuyển đổi một danh sách Lecturer (Entity) sang danh sách LecturerDTO.
     */
    List<LecturerDTO> toDtoList(List<Lecturer> lecturers);

    /**
     * Chuyển đổi từ CreateLecturerRequestDTO (dữ liệu đầu vào) sang Lecturer (Entity).
     * Bỏ qua các trường phức tạp sẽ được xử lý thủ công trong service.
     */
    @Mapping(target = "password", ignore = true) // Mật khẩu sẽ được mã hóa và set trong service
    @Mapping(target = "department", ignore = true) // Department sẽ được tìm và set trong service
    @Mapping(target = "lecturerId", ignore = true) // ID sẽ do database tự tạo
    @Mapping(target = "assignments", ignore = true) // Bỏ qua các mối quan hệ khác
    Lecturer fromCreateDtoToEntity(CreateLecturerRequestDTO createDto);
}
