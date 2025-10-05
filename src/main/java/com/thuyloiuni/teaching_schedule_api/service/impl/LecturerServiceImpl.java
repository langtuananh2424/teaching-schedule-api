package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.LecturerMapper;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: tự động tạo constructor cho các trường final
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final LecturerMapper lecturerMapper;

    @Override
    @Transactional
    public LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO) {
        // Kiểm tra sự tồn tại (logic này rất tốt và giữ nguyên)
        if (lecturerRepository.existsByLecturerCode(lecturerRequestDTO.getLecturerCode())) {
            throw new IllegalArgumentException("Mã giảng viên '" + lecturerRequestDTO.getLecturerCode() + "' đã tồn tại.");
        }
        if (lecturerRepository.existsByEmail(lecturerRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email '" + lecturerRequestDTO.getEmail() + "' đã tồn tại.");
        }

        // Tìm Department
        Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + lecturerRequestDTO.getDepartmentId()));

        // Ánh xạ từ DTO sang Entity bằng MapStruct
        Lecturer lecturer = lecturerMapper.fromCreateDtoToEntity(lecturerRequestDTO);

        // Xử lý các trường phức tạp thủ công
        lecturer.setDepartment(department);
        lecturer.setPassword(passwordEncoder.encode(lecturerRequestDTO.getPassword()));

        // Gán vai trò (nếu có trong DTO) hoặc mặc định là LECTURER
        lecturer.setRole(lecturerRequestDTO.getRole() != null ? lecturerRequestDTO.getRole() : RoleType.LECTURER);

        // Lưu và trả về DTO đã được map
        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        return lecturerMapper.toDto(savedLecturer);
    }

    @Override
    @Transactional
    public LecturerDTO updateLecturer(Integer id, CreateLecturerRequestDTO lecturerRequestDTO) {
        Lecturer existingLecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id));

        // Kiểm tra unique constraints cho code và email nếu chúng thay đổi
        if (StringUtils.hasText(lecturerRequestDTO.getLecturerCode()) && !existingLecturer.getLecturerCode().equals(lecturerRequestDTO.getLecturerCode())) {
            if (lecturerRepository.existsByLecturerCode(lecturerRequestDTO.getLecturerCode())) {
                throw new IllegalArgumentException("Mã giảng viên '" + lecturerRequestDTO.getLecturerCode() + "' đã được sử dụng.");
            }
            existingLecturer.setLecturerCode(lecturerRequestDTO.getLecturerCode());
        }

        if (StringUtils.hasText(lecturerRequestDTO.getEmail()) && !existingLecturer.getEmail().equals(lecturerRequestDTO.getEmail())) {
            if (lecturerRepository.existsByEmail(lecturerRequestDTO.getEmail())) {
                throw new IllegalArgumentException("Email '" + lecturerRequestDTO.getEmail() + "' đã được sử dụng.");
            }
            existingLecturer.setEmail(lecturerRequestDTO.getEmail());
        }

        // Cập nhật các trường đơn giản
        if (StringUtils.hasText(lecturerRequestDTO.getFullName())) {
            existingLecturer.setFullName(lecturerRequestDTO.getFullName());
        }

        // Cập nhật Department nếu ID được cung cấp và khác ID hiện tại
        if (lecturerRequestDTO.getDepartmentId() != null && !existingLecturer.getDepartment().getDepartmentId().equals(lecturerRequestDTO.getDepartmentId())) {
            Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + lecturerRequestDTO.getDepartmentId()));
            existingLecturer.setDepartment(department);
        }

        // Cập nhật vai trò
        if (lecturerRequestDTO.getRole() != null) {
            existingLecturer.setRole(lecturerRequestDTO.getRole());
        }

        // Không cho phép cập nhật mật khẩu qua API này. Tạo một API riêng cho việc "đổi mật khẩu".

        Lecturer updatedLecturer = lecturerRepository.save(existingLecturer);
        return lecturerMapper.toDto(updatedLecturer);
    }

    @Override
    @Transactional
    public void deleteLecturer(Integer id) {
        if (!lecturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + id);
        }
        // Thêm logic kiểm tra ràng buộc trước khi xóa nếu cần
        lecturerRepository.deleteById(id);
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
        return lecturerRepository.findByEmail(email)
                .map(lecturerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với email: " + email));
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
        List<Lecturer> lecturers = lecturerRepository.findByRole(role);
        return lecturerMapper.toDtoList(lecturers);
    }
}
