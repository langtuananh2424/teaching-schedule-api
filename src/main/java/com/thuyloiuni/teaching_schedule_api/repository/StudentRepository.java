// src/main/java/com/thuyloiuni/teaching_schedule_api/repository/StudentRepository.java
package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Student; // <<== Sửa lại Entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// <<== SỬA 2: Sửa lại tên interface và kiểu Generic
public interface StudentRepository extends JpaRepository<Student, Integer> {

    /**
     * Tìm kiếm sinh viên theo mã số sinh viên.
     */
    Optional<Student> findByStudentCode(String studentCode);

    /**
     * Kiểm tra sự tồn tại của sinh viên theo mã số.
     */
    boolean existsByStudentCode(String studentCode);

    /**
     * Tìm danh sách sinh viên theo ID của lớp học.
     * Spring Data JPA sẽ tự động hiểu và join qua trường `studentClass`.
     */
    List<Student> findByStudentClass_ClassId(Integer classId);
}
