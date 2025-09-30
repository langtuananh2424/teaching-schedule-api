package com.thuyloiuni.teaching_schedule_api.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;

@Service
public class LecturerServiceImpl implements LecturerService {
    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LecturerServiceImpl(LecturerRepository lecturerRepository, DepartmentRepository departmentRepository, ModelMapper modelMapper) {
        this.lecturerRepository = lecturerRepository;
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    private LecturerDTO mapToLecturerDTO(Lecturer lecturer) {
        if(lecturer == null) {
            return null;
        }
        LecturerDTO dto = modelMapper.map(lecturer, LecturerDTO.class);
        if (lecturer.getDepartment() != null) {
            dto.setDepartmentId(lecturer.getDepartment().getDepartmentId());
            dto.setDepartmentName(lecturer.getDepartment().getDepartmentName());
        }
        return dto;
    }
    
    @Override
    @Transactional
    public LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO) {
        if (lecturerRepository.findByLecturerCode(lecturerRequestDTO.getLecturerCode()).isPresent()) {
            throw new IllegalArgumentException("Lecturer code already exists");
        }
        if (lecturerRepository.findByEmail(lecturerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + lecturerRequestDTO.getDepartmentId()));

        Lecturer lecturer = modelMapper.map(lecturerRequestDTO, Lecturer.class);
        lecturer.setDepartment(department);

        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        return mapToLecturerDTO(savedLecturer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LecturerDTO> getLecturerById(Integer id) {
        return lecturerRepository.findById(id)
                .map(this::mapToLecturerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LecturerDTO> getLecturerByEmail(String email) {
        return lecturerRepository.findByEmail(email)
                .map(this::mapToLecturerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LecturerDTO> getLecturerByCode(String code) {
        return lecturerRepository.findByLecturerCode(code)
                .map(this::mapToLecturerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getAllLecturers() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        return lecturers.stream()
                .map(this::mapToLecturerDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersByDepartmentId(Integer departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        return lecturerRepository.findByDepartment_DepartmentId(departmentId).stream()
                .map(this::mapToLecturerDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersByRole(RoleType role) {
        return lecturerRepository.findByRole(role).stream()
                .map(this::mapToLecturerDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LecturerDTO updateLecturer(Integer id, CreateLecturerRequestDTO lecturerRequestDTO) {
        Lecturer existingLecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));

        if (!existingLecturer.getLecturerCode().equals(lecturerRequestDTO.getLecturerCode()) &&
                lecturerRepository.findByLecturerCode(lecturerRequestDTO.getLecturerCode()).isPresent()) {
            throw new IllegalArgumentException("Lecturer code already exists");
        }
        if (!existingLecturer.getEmail().equals(lecturerRequestDTO.getEmail()) &&
                lecturerRepository.findByEmail(lecturerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Department departmentId = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + lecturerRequestDTO.getDepartmentId()));

        existingLecturer.setLecturerCode(lecturerRequestDTO.getLecturerCode());
        existingLecturer.setFullName(lecturerRequestDTO.getFullName());
        existingLecturer.setEmail(lecturerRequestDTO.getEmail());
        existingLecturer.setRole(lecturerRequestDTO.getRole());
        existingLecturer.setDepartment(departmentId);

        Lecturer updatedLecturer = lecturerRepository.save(existingLecturer);
        return mapToLecturerDTO(updatedLecturer);
    }

    @Override
    @Transactional
    public void deleteLecturer(Integer id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));

        lecturerRepository.delete(lecturer);
    }

    
}
