package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdatePasswordDTO;
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

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final LecturerMapper lecturerMapper;

    @Override
    @Transactional
    public LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO) {
        if (userRepository.findByEmail(lecturerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + lecturerRequestDTO.getEmail() + "' đã tồn tại.");
        }
        if (lecturerRepository.existsByLecturerCode(lecturerRequestDTO.getLecturerCode())) {
            throw new IllegalArgumentException("Mã giảng viên '" + lecturerRequestDTO.getLecturerCode() + "' đã tồn tại.");
        }

        // 1. Create and save the User
        User newUser = new User();
        newUser.setEmail(lecturerRequestDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(lecturerRequestDTO.getPassword()));
        newUser.setRole(lecturerRequestDTO.getRole() != null ? lecturerRequestDTO.getRole() : RoleType.LECTURER);
        User savedUser = userRepository.save(newUser);

        // 2. Create and save the Lecturer, linking it to the User
        Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + lecturerRequestDTO.getDepartmentId()));

        Lecturer newLecturer = new Lecturer();
        newLecturer.setLecturerCode(lecturerRequestDTO.getLecturerCode());
        newLecturer.setFullName(lecturerRequestDTO.getFullName());
        newLecturer.setDepartment(department);
        newLecturer.setUser(savedUser);

        Lecturer savedLecturer = lecturerRepository.save(newLecturer);

        return lecturerMapper.toDto(savedLecturer);
    }

    @Override
    @Transactional
    public LecturerDTO updateLecturer(Integer id, CreateLecturerRequestDTO lecturerRequestDTO) {
        Lecturer existingLecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id));

        User associatedUser = existingLecturer.getUser();

        if (StringUtils.hasText(lecturerRequestDTO.getEmail()) && !associatedUser.getEmail().equals(lecturerRequestDTO.getEmail())) {
            if (userRepository.findByEmail(lecturerRequestDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email '" + lecturerRequestDTO.getEmail() + "' đã được sử dụng.");
            }
            associatedUser.setEmail(lecturerRequestDTO.getEmail());
        }

        if (StringUtils.hasText(lecturerRequestDTO.getLecturerCode()) && !existingLecturer.getLecturerCode().equals(lecturerRequestDTO.getLecturerCode())) {
            if (lecturerRepository.existsByLecturerCode(lecturerRequestDTO.getLecturerCode())) {
                throw new IllegalArgumentException("Mã giảng viên '" + lecturerRequestDTO.getLecturerCode() + "' đã được sử dụng.");
            }
            existingLecturer.setLecturerCode(lecturerRequestDTO.getLecturerCode());
        }

        if (StringUtils.hasText(lecturerRequestDTO.getFullName())) {
            existingLecturer.setFullName(lecturerRequestDTO.getFullName());
        }
        
        if (lecturerRequestDTO.getRole() != null) {
            associatedUser.setRole(lecturerRequestDTO.getRole());
        }

        if (lecturerRequestDTO.getDepartmentId() != null && !existingLecturer.getDepartment().getDepartmentId().equals(lecturerRequestDTO.getDepartmentId())) {
            Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + lecturerRequestDTO.getDepartmentId()));
            existingLecturer.setDepartment(department);
        }

        userRepository.save(associatedUser);
        Lecturer updatedLecturer = lecturerRepository.save(existingLecturer);
        return lecturerMapper.toDto(updatedLecturer);
    }

    @Override
    @Transactional
    public void deleteLecturer(Integer id) {
        // This logic assumes that deleting a lecturer should also delete the associated user.
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id));
        
        lecturerRepository.delete(lecturer);
        
        if (lecturer.getUser() != null) {
            userRepository.delete(lecturer.getUser());
        }
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
    
    // This method is no longer suitable as email is in the User entity.
    // A new method in a proper UserService would be needed to get a user by email.
    @Override
    @Transactional(readOnly = true)
    public LecturerDTO getLecturerByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));
        if (user.getLecturer() == null) {
            throw new ResourceNotFoundException("Người dùng với email: " + email + " không phải là giảng viên.");
        }
        return lecturerMapper.toDto(user.getLecturer());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getAllLecturers() {
        return lecturerMapper.toDtoList(lecturerRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersByDepartmentId(Integer departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Không tìm thấy khoa với ID: " + departmentId);
        }
        List<Lecturer> lecturers = lecturerRepository.findByDepartment_DepartmentId(departmentId);
        return lecturerMapper.toDtoList(lecturers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersByRole(RoleType role) {
        List<User> users = userRepository.findByRole(role);
        List<Lecturer> lecturers = users.stream()
                                        .map(User::getLecturer)
                                        .filter(java.util.Objects::nonNull)
                                        .collect(java.util.stream.Collectors.toList());
        return lecturerMapper.toDtoList(lecturers);
    }

    @Override
    @Transactional
    public void updatePassword(Integer id, UpdatePasswordDTO passwordDTO) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id));

        User user = lecturer.getUser();
        if (user == null) {
            throw new IllegalStateException("Giảng viên này không có tài khoản người dùng được liên kết.");
        }

        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác.");
        }

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
    }
}
