// src/main/java/com/thuyloiuni/teaching_schedule_api/mapper/DepartmentMapper.java
package com.thuyloiuni.teaching_schedule_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;

import java.util.List;
/**
 * Mapper để chuyển đổi giữa Department Entity và DepartmentDTO.
 * Sử dụng MapStruct và được quản lý bởi Spring.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    /**
     * Chuyển đổi từ Department (Entity) sang DepartmentDTO.Java
     * @param department Đối tượng Entity.
     * @return Đối tượng DTO tương ứng.
     */
    DepartmentDTO toDto(Department department);

    /**
     * Chuyển đổi từ DepartmentDTO sang Department (Entity).
     * Bỏ qua các mối quan hệ 'OneToMany' vì chúng không nên được quản lý trực tiếp từ DTO.
     * Dữ liệu về giảng viên hoặc môn học thuộc về khoa sẽ được quản lý riêng.
     *
     * @param departmentDTO Đối tượng DTO.
     * @return Đối tượng Entity tương ứng.
     */
    @Mapping(target = "lecturers", ignore = true) // Bỏ qua việc map danh sách lecturers từ DTO
    @Mapping(target = "subjects", ignore = true)  // Bỏ qua việc map danh sách subjects từ DTO
    Department toEntity(DepartmentDTO departmentDTO);
    /**
     * Chuyển đổi một danh sách DepartmentJava(Entity) sang danh sách DepartmentDTO.
     * MapStruct sẽ tự động sử dụng phương thức 'toDto' cho mỗi phần tử.
     * @param departments Danh sách các Entity.
     * @return Danh sách các DTO tương ứng.
     */
    List<DepartmentDTO> toDtoList(List<DepartmentJava> departments);

}
