package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Student;
import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
     */
    List<Student> findByStudentClass_ClassId(Integer classId);

    /**
     * Tìm danh sách sinh viên theo đối tượng Lớp học.
     * Được sử dụng bởi DataSeeder.
     */
    List<Student> findByStudentClass(StudentClass studentClass);
}
