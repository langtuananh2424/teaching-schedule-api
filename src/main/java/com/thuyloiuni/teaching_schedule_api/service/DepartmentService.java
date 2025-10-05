package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;
import java.util.List;

public interface DepartmentService {

    /**
     * Lấy danh sách tất cả các khoa.
     * @return Danh sách các DepartmentDTO.
     */
    List<DepartmentDTO> getAllDepartments();

    /**
     * Lấy thông tin một khoa theo ID.
     * @param id ID của khoa.
     * @return DepartmentDTO tìm thấy.
     * @throws com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException nếu không tìm thấy.
     */
    DepartmentDTO getDepartmentById(Integer id);

    /**
     * Lấy thông tin một khoa theo tên.
     * @param name Tên của khoa.
     * @return DepartmentDTO tìm thấy.
     * @throws com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException nếu không tìm thấy.
     */
    DepartmentDTO getDepartmentByName(String name);

    /**
     * Tạo một khoa mới.
     * @param departmentDTO Dữ liệu của khoa mới.
     * @return DepartmentDTO đã được lưu.
     */
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    /**
     * Cập nhật thông tin một khoa.
     * @param id ID của khoa cần cập nhật.
     * @param departmentDTO Dữ liệu mới.
     * @return DepartmentDTO sau khi cập nhật.
     */
    DepartmentDTO updateDepartment(Integer id, DepartmentDTO departmentDTO);

    /**
     * Xóa một khoa theo ID.
     * @param id ID của khoa cần xóa.
     */
    void deleteDepartment(Integer id);
}
