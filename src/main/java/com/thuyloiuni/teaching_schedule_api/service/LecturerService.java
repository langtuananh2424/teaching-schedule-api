package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;

import java.util.List;

public interface LecturerService {

    /**
     * Tạo một giảng viên mới.
     * @param lecturerRequestDTO Dữ liệu để tạo giảng viên.
     * @return DTO của giảng viên đã được tạo.
     */
    LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO);

    /**
     * Cập nhật thông tin của một giảng viên.
     * @param id ID của giảng viên cần cập nhật.
     * @param lecturerRequestDTO Dữ liệu mới.
     * @return DTO của giảng viên sau khi cập nhật.
     */
    LecturerDTO updateLecturer(Integer id, CreateLecturerRequestDTO lecturerRequestDTO);

    /**
     * Xóa một giảng viên.
     * @param id ID của giảng viên cần xóa.
     */
    void deleteLecturer(Integer id);

    /**
     * Lấy thông tin giảng viên theo ID.
     * @param id ID của giảng viên.
     * @return DTO của giảng viên tìm thấy.
     */
    LecturerDTO getLecturerById(Integer id);

    /**
     * Lấy thông tin giảng viên theo mã giảng viên.
     * @param code Mã giảng viên.
     * @return DTO của giảng viên tìm thấy.
     */
    LecturerDTO getLecturerByCode(String code);

    /**
     * Lấy thông tin giảng viên theo email.
     * @param email Email của giảng viên.
     * @return DTO của giảng viên tìm thấy.
     */
    LecturerDTO getLecturerByEmail(String email);

    /**
     * Lấy danh sách tất cả giảng viên.
     * @return Danh sách DTO của tất cả giảng viên.
     */
    List<LecturerDTO> getAllLecturers();

    /**
     * Lấy danh sách giảng viên theo Khoa.
     * @param departmentId ID của Khoa.
     * @return Danh sách DTO của các giảng viên thuộc Khoa đó.
     */
    List<LecturerDTO> getLecturersByDepartmentId(Integer departmentId);

    /**
     * Lấy danh sách giảng viên theo vai trò.
     * @param role Vai trò cần lọc.
     * @return Danh sách DTO của các giảng viên có vai trò đó.
     */
    List<LecturerDTO> getLecturersByRole(RoleType role);
}

