// D:/MyCode/Java/teaching-schedule-api/src/main/java/com/thuyloiuni/teaching_schedule_api/repository/SubjectRepository.java

package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Subject; // <<== Sửa lại Entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> { // <<== Sửa lại tên và kiểu Generic

    /**
     * Tìm kiếm một môn học dựa trên mã môn học.
     * Được sử dụng trong service để kiểm tra sự tồn tại.
     *
     * @param subjectCode Mã môn học cần tìm.
     * @return một Optional chứa Subject nếu tìm thấy.
     */
    Optional<Subject> findBySubjectCode(String subjectCode); // <<== Sửa lại phương thức truy vấn

    /**
     * Kiểm tra xem một mã môn học đã tồn tại trong DB hay chưa.
     * Hiệu năng tốt hơn findBySubjectCode().isPresent().
     *
     * @param subjectCode Mã môn học cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    boolean existsBySubjectCode(String subjectCode); // <<== Sửa lại phương thức truy vấn
}
