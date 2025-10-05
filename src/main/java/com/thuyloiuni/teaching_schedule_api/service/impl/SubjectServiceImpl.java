package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Subject;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.SubjectMapper;
import com.thuyloiuni.teaching_schedule_api.repository.SubjectRepository;
import com.thuyloiuni.teaching_schedule_api.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    // Cập nhật constructor để inject SubjectMapper
    public SubjectServiceImpl(SubjectRepository subjectRepository, SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        // Sử dụng phương thức toDtoList của mapper
        return subjectMapper.toDtoList(subjects);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(int id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với ID: " + id));
        // Sử dụng mapper để chuyển đổi
        return subjectMapper.toDto(subject);
    }

    @Override
    @Transactional
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {
        if (subjectRepository.existsBySubjectCode(subjectDTO.getSubjectCode())) {
            throw new IllegalArgumentException("Mã môn học '" + subjectDTO.getSubjectCode() + "' đã tồn tại.");
        }

        // Chuyển DTO thành Entity bằng mapper
        Subject subject = subjectMapper.toEntity(subjectDTO);
        Subject savedSubject = subjectRepository.save(subject);

        // Chuyển Entity đã lưu thành DTO để trả về
        return subjectMapper.toDto(savedSubject);
    }

    @Override
    @Transactional
    public SubjectDTO updateSubject(int id, SubjectDTO subjectDTO) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với ID: " + id));

        // Cập nhật các trường
        existingSubject.setSubjectCode(subjectDTO.getSubjectCode());
        existingSubject.setSubjectName(subjectDTO.getSubjectName());
        existingSubject.setCreditHours(subjectDTO.getCreditHours());

        Subject updatedSubject = subjectRepository.save(existingSubject);
        return subjectMapper.toDto(updatedSubject);
    }

    @Override
    @Transactional
    public void deleteSubject(int id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy môn học với ID: " + id);
        }
        subjectRepository.deleteById(id);
    }
}
