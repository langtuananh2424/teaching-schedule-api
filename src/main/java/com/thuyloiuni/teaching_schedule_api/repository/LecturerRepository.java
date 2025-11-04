package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {

    /**
     * Tìm kiếm một giảng viên dựa trên mã giảng viên.
     *
     * @param lecturerCode Mã giảng viên cần tìm.
     * @return một Optional chứa Lecturer nếu tìm thấy.
     */
    Optional<Lecturer> findByLecturerCode(String lecturerCode);

    /**
     * Kiểm tra xem một mã giảng viên đã tồn tại trong DB hay chưa.
     *
     * @param lecturerCode Mã giảng viên cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    boolean existsByLecturerCode(String lecturerCode);

    /**
     * Tìm danh sách giảng viên theo ID của khoa.
     *
     * @param departmentId ID của khoa.
     * @return Danh sách giảng viên thuộc khoa đó.
     */
    List<Lecturer> findByDepartment_DepartmentId(Integer departmentId);

}
