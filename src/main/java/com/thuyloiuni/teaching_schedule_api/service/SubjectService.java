package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import java.util.List;

public interface SubjectService {

    /**
     * Lấy danh sách tất cả các môn học.
     * @return Danh sách SubjectDTO.
     */
    List<SubjectDTO> getAllSubjects();

    /**
     * Tìm một môn học theo ID.
     * @param id ID của môn học.
     * @return SubjectDTO tìm thấy.
     */
    SubjectDTO getSubjectById(int id);

    /**
     * Tạo một môn học mới.
     * @param subjectDTO Dữ liệu môn học mới.
     * @return SubjectDTO đã được lưu.
     */
    SubjectDTO createSubject(SubjectDTO subjectDTO);

    /**
     * Cập nhật thông tin một môn học.
     * @param id ID của môn học cần cập nhật.
     * @param subjectDTO Dữ liệu mới của môn học.
     * @return SubjectDTO sau khi cập nhật.
     */
    SubjectDTO updateSubject(int id, SubjectDTO subjectDTO);

    /**
     * Xóa một môn học theo ID.
     * @param id ID của môn học cần xóa.
     */
    void deleteSubject(int id);
}
