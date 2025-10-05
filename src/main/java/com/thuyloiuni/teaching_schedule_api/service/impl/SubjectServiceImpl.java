package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Subject;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.repository.SubjectRepository;
import com.thuyloiuni.teaching_schedule_api.service.SubjectService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    public SubjectServiceImpl(SubjectRepository subjectRepository, ModelMapper modelMapper) {
        this.subjectRepository = subjectRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        return subjects.stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(int id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với ID: " + id));
        return modelMapper.map(subject, SubjectDTO.class);
    }

    @Override
    @Transactional
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {
        // Kiểm tra xem mã môn học đã tồn tại chưa
        if (subjectRepository.existsBySubjectCode(subjectDTO.getSubjectCode())) {
            throw new IllegalArgumentException("Mã môn học '" + subjectDTO.getSubjectCode() + "' đã tồn tại.");
        }

        Subject subject = modelMapper.map(subjectDTO, Subject.class);
        Subject savedSubject = subjectRepository.save(subject);
        return modelMapper.map(savedSubject, SubjectDTO.class);
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
        return modelMapper.map(updatedSubject, SubjectDTO.class);
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
