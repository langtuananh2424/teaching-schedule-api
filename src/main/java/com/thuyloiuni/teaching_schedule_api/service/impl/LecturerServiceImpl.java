package com.thuyloiuni.teaching_schedule_api.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thuyloiuni.teaching_schedule_api.dto.CreateLecturerRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer; // Sử dụng Lombok để tiện lợi
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.service.LecturerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Lombok: tự động tạo constructor cho các trường final
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper; // Được inject từ Spring context
    // private final PasswordEncoder passwordEncoder; // Bỏ comment nếu dùng

    // Helper method để map từ Lecturer Entity sang LecturerDTO
    // Bạn đã có phương thức này trong code trước, giữ lại hoặc cải tiến nếu cần
    private LecturerDTO mapToLecturerDTO(Lecturer lecturer) {
        if (lecturer == null) {
            return null;
        }
        LecturerDTO dto = modelMapper.map(lecturer, LecturerDTO.class);
        if (lecturer.getDepartment() != null) {
            // ModelMapper có thể đã map lecturer.department.departmentId sang dto.departmentId
            // nếu tên trường trong DTO khớp (ví dụ: departmentId) hoặc có cấu hình.
            // Nếu không, bạn cần set thủ công như cũ hoặc cấu hình ModelMapper.
            dto.setDepartmentId(lecturer.getDepartment().getDepartmentId());
            dto.setDepartmentName(lecturer.getDepartment().getDepartmentName());
        }
        if (lecturer.getRole() != null) {
            dto.setRole(lecturer.getRole()); // Chuyển Enum RoleType sang String
        }
        // dto.setId(lecturer.getLecturerId()); // ModelMapper thường tự map nếu tên gần giống
        return dto;
    }


    @Override
    @Transactional // Đảm bảo tính nhất quán dữ liệu
    public LecturerDTO createLecturer(CreateLecturerRequestDTO lecturerRequestDTO) {
        // Kiểm tra sự tồn tại của lecturer code
        if (lecturerRepository.findByLecturerCode(lecturerRequestDTO.getLecturerCode()).isPresent()) {
            throw new IllegalArgumentException("Lecturer code '" + lecturerRequestDTO.getLecturerCode() + "' already exists.");
        }
        // Kiểm tra sự tồn tại của email
        if (lecturerRepository.findByEmail(lecturerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + lecturerRequestDTO.getEmail() + "' already exists.");
        }

        // Tìm Department
        Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + lecturerRequestDTO.getDepartmentId()));

        // Map từ DTO sang Entity Lecturer
        Lecturer lecturer = modelMapper.map(lecturerRequestDTO, Lecturer.class);
        lecturer.setDepartment(department);

        // Xử lý mật khẩu (NẾU CÓ)
        // if (lecturerRequestDTO.getPassword() != null && !lecturerRequestDTO.getPassword().isEmpty()) {
        //     lecturer.setPassword(passwordEncoder.encode(lecturerRequestDTO.getPassword()));
        // } else {
        //     throw new IllegalArgumentException("Password is required for new lecturer.");
        // }


        // Xử lý RoleType (nếu CreateLecturerRequestDTO có trường role dạng String)
        // Nếu không có trường role trong DTO, bạn có thể gán mặc định
        if (lecturerRequestDTO.getRole() != null) {
             try {
                lecturer.setRole(RoleType.valueOf(lecturerRequestDTO.getRole().name().toUpperCase())); // Giả sử DTO có getRole() trả về Enum
             } catch (IllegalArgumentException e) {
                 throw new IllegalArgumentException("Invalid role provided: " + lecturerRequestDTO.getRole().name());
             }
        } else {
            lecturer.setRole(RoleType.LECTURER); // Gán vai trò mặc định
        }


        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        return mapToLecturerDTO(savedLecturer);
    }

    @Override
    @Transactional(readOnly = true) // readOnly = true cho các thao tác chỉ đọc để tối ưu
    public Optional<LecturerDTO> getLecturerById(Integer id) {
        return lecturerRepository.findById(id).map(this::mapToLecturerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LecturerDTO> getLecturerByEmail(String email) {
        return lecturerRepository.findByEmail(email).map(this::mapToLecturerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LecturerDTO> getLecturerByCode(String code) {
        return lecturerRepository.findByLecturerCode(code).map(this::mapToLecturerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getAllLecturers() {
        return lecturerRepository.findAll().stream()
                .map(this::mapToLecturerDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersByDepartmentId(Integer departmentId) {
        // Kiểm tra Department có tồn tại không trước khi truy vấn Lecturer
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found with id: " + departmentId);
        }
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

        // Kiểm tra nếu lecturer code thay đổi và code mới đã tồn tại cho giảng viên khác
        if (lecturerRequestDTO.getLecturerCode() != null &&
            !existingLecturer.getLecturerCode().equals(lecturerRequestDTO.getLecturerCode()) &&
            lecturerRepository.findByLecturerCode(lecturerRequestDTO.getLecturerCode()).filter(l -> !l.getLecturerId().equals(id)).isPresent()) {
            throw new IllegalArgumentException("Lecturer code '" + lecturerRequestDTO.getLecturerCode() + "' already exists for another lecturer.");
        }

        // Kiểm tra nếu email thay đổi và email mới đã tồn tại cho giảng viên khác
        if (lecturerRequestDTO.getEmail() != null &&
            !existingLecturer.getEmail().equals(lecturerRequestDTO.getEmail()) &&
            lecturerRepository.findByEmail(lecturerRequestDTO.getEmail()).filter(l -> !l.getLecturerId().equals(id)).isPresent()) {
            throw new IllegalArgumentException("Email '" + lecturerRequestDTO.getEmail() + "' already exists for another lecturer.");
        }

        // Cập nhật các trường từ DTO
        // ModelMapper có thể được cấu hình để map có điều kiện hoặc bỏ qua null
        // Nếu không, bạn cần kiểm tra null thủ công cho từng trường
        if (lecturerRequestDTO.getLecturerCode() != null) {
            existingLecturer.setLecturerCode(lecturerRequestDTO.getLecturerCode());
        }
        if (lecturerRequestDTO.getFullName() != null) {
            existingLecturer.setFullName(lecturerRequestDTO.getFullName());
        }
        if (lecturerRequestDTO.getEmail() != null) {
            existingLecturer.setEmail(lecturerRequestDTO.getEmail());
        }

        // Cập nhật Department nếu departmentId được cung cấp và khác
        if (lecturerRequestDTO.getDepartmentId() != null &&
            (existingLecturer.getDepartment() == null || !existingLecturer.getDepartment().getDepartmentId().equals(lecturerRequestDTO.getDepartmentId()))) {
            Department department = departmentRepository.findById(lecturerRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + lecturerRequestDTO.getDepartmentId()));
            existingLecturer.setDepartment(department);
        }

        // Cập nhật RoleType nếu được cung cấp
        if (lecturerRequestDTO.getRole() != null) {
            try {
                existingLecturer.setRole(RoleType.valueOf(lecturerRequestDTO.getRole().name().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role provided: " + lecturerRequestDTO.getRole().name());
            }
        }
        
        // KHÔNG cập nhật mật khẩu ở đây trừ khi bạn có logic rõ ràng và `CreateLecturerRequestDTO` có trường password
        // Nếu cho phép cập nhật mật khẩu, cần mã hóa lại:
        // if (lecturerRequestDTO.getPassword() != null && !lecturerRequestDTO.getPassword().isEmpty()) {
        //    existingLecturer.setPassword(passwordEncoder.encode(lecturerRequestDTO.getPassword()));
        // }

        Lecturer updatedLecturer = lecturerRepository.save(existingLecturer);
        return mapToLecturerDTO(updatedLecturer);
    }

    @Override
    @Transactional
    public void deleteLecturer(Integer id) {
        if (!lecturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lecturer not found with id: " + id);
        }
        // Trước khi xóa, bạn có thể cần kiểm tra các ràng buộc khóa ngoại
        // Ví dụ: Giảng viên có đang được gán cho Lịch giảng nào không?
        // Nếu có, bạn có thể muốn ngăn chặn việc xóa hoặc xử lý logic phù hợp.
        lecturerRepository.deleteById(id);
    }
}
