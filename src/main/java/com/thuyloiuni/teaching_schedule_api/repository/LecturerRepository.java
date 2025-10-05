package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {

    /**
     * Tìm kiếm một giảng viên dựa trên mã giảng viên.
     * Được sử dụng trong service để kiểm tra sự tồn tại trước khi tạo mới.
     *
     * @param lecturerCode Mã giảng viên cần tìm.
     * @return một Optional chứa Lecturer nếu tìm thấy.
     */
    Optional<Lecturer> findByLecturerCode(String lecturerCode);

    /**
     * Tìm kiếm một giảng viên dựa trên email.
     * Được sử dụng trong service để kiểm tra sự tồn tại và cho logic đăng nhập.
     *
     * @param email Email cần tìm.
     * @return một Optional chứa Lecturer nếu tìm thấy.
     */
    Optional<Lecturer> findByEmail(String email);

    /**
     * Kiểm tra xem một mã giảng viên đã tồn tại trong DB hay chưa.
     * Hiệu năng tốt hơn findByLecturerCode().isPresent() vì chỉ cần câu lệnh SELECT COUNT.
     *
     * @param lecturerCode Mã giảng viên cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    boolean existsByLecturerCode(String lecturerCode);

    /**
     * Kiểm tra xem một email đã tồn tại trong DB hay chưa.
     *
     * @param email Email cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    boolean existsByEmail(String email);
}
