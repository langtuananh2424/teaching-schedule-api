package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.User;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.LecturerMapper;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.UserRepository;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final LecturerMapper lecturerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO) {
        if (userRepository.findByEmail(lecturerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }

        User newUser = new User();
        newUser.setEmail(lecturerRequestDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(lecturerRequestDTO.getPassword()));
        newUser.setRole(lecturerRequestDTO.getRole());

        Lecturer newLecturer = new Lecturer();
        newLecturer.setLecturerCode(lecturerRequestDTO.getLecturerCode());
        newLecturer.setFullName(lecturerRequestDTO.getFullName());

        Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + lecturerRequestDTO.getDepartmentId()));
        newLecturer.setDepartment(department);
        newLecturer.setUser(newUser);
        newUser.setLecturer(newLecturer);

        Lecturer savedLecturer = lecturerRepository.save(newLecturer);
        return lecturerMapper.toDto(savedLecturer);
    }

    @Override
    @Transactional
    public LecturerDTO updateLecturer(Integer id, CreateLecturerRequestDTO lecturerRequestDTO) {
        Lecturer existingLecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id));

        if (StringUtils.hasText(lecturerRequestDTO.getLecturerCode())) {
            existingLecturer.setLecturerCode(lecturerRequestDTO.getLecturerCode());
        }
        if (StringUtils.hasText(lecturerRequestDTO.getFullName())) {
            existingLecturer.setFullName(lecturerRequestDTO.getFullName());
        }

        if (lecturerRequestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + lecturerRequestDTO.getDepartmentId()));
            existingLecturer.setDepartment(department);
        }

        Lecturer updatedLecturer = lecturerRepository.save(existingLecturer);
        return lecturerMapper.toDto(updatedLecturer);
    }

    @Override
    @Transactional
    public void deleteLecturer(Integer id) {
        Lecturer lecturerToDelete = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id));
        lecturerRepository.delete(lecturerToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerDTO getLecturerById(Integer id) {
        return lecturerRepository.findById(id)
                .map(lecturerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerDTO getLecturerByCode(String code) {
        return lecturerRepository.findByLecturerCode(code)
                .map(lecturerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với mã: " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerDTO getLecturerByEmail(String email) {
        return lecturerRepository.findByUser_Email(email)
                .map(lecturerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getAllLecturers() {
        return lecturerRepository.findAll().stream()
                .map(lecturerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersByDepartmentId(Integer departmentId) {
        return lecturerRepository.findByDepartment_DepartmentId(departmentId).stream()
                .map(lecturerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersByRole(RoleType role) {
        return lecturerRepository.findByUser_Role(role).stream()
                .map(lecturerMapper::toDto)
                .collect(Collectors.toList());
    }
}
