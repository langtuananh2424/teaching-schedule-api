// src/main/java/com/thuyloiuni/teaching_schedule_api/service/impl/StudentClassServiceImpl.java
package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.StudentClassMapper;
import com.thuyloiuni.teaching_schedule_api.repository.StudentClassRepository;
import com.thuyloiuni.teaching_schedule_api.service.StudentClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentClassServiceImpl implements StudentClassService {

    private final StudentClassRepository studentClassRepository;
    private final StudentClassMapper studentClassMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StudentClassDTO> getAllClasses() {
        return studentClassMapper.toDtoList(studentClassRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentClassDTO getClassById(Integer id) {
        return studentClassRepository.findById(id)
                .map(studentClassMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + id));
    }

    @Override
    @Transactional
    public StudentClassDTO createClass(StudentClassDTO studentClassDTO) {
        if (studentClassRepository.existsByClassCode(studentClassDTO.getClassCode())) {
            throw new IllegalArgumentException("Mã lớp '" + studentClassDTO.getClassCode() + "' đã tồn tại.");
        }

        StudentClass newClass = studentClassMapper.toEntity(studentClassDTO);
        StudentClass savedClass = studentClassRepository.save(newClass);
        return studentClassMapper.toDto(savedClass);
    }

    @Override
    @Transactional
    public StudentClassDTO updateClass(Integer id, StudentClassDTO studentClassDTO) {
        StudentClass existingClass = studentClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + id));

        // Cập nhật các trường
        existingClass.setClassCode(studentClassDTO.getClassCode());
        existingClass.setClassName(studentClassDTO.getClassName());
        existingClass.setSemester(studentClassDTO.getSemester());

        StudentClass updatedClass = studentClassRepository.save(existingClass);
        return studentClassMapper.toDto(updatedClass);
    }

    @Override
    @Transactional
    public void deleteClass(Integer id) {
        if (!studentClassRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + id);
        }
        studentClassRepository.deleteById(id);
    }
}
