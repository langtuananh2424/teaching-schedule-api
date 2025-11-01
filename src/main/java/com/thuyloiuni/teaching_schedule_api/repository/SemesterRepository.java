package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {

    /**
     * Lấy danh sách các năm học không trùng lặp, được sắp xếp giảm dần.
     * Dùng để điền dữ liệu cho dropdown chọn năm học.
     *
     * @return Danh sách các chuỗi năm học (VD: ["2024-2025", "2023-2024"])
     */
    @Query("SELECT DISTINCT s.academicYear FROM Semester s ORDER BY s.academicYear DESC")
    List<String> findDistinctAcademicYears();

    /**
     * Lấy danh sách các học kỳ dựa trên một năm học cụ thể.
     *
     * @param academicYear Năm học cần tìm
     * @return Danh sách các entity Semester
     */
    List<Semester> findByAcademicYear(String academicYear);
}
