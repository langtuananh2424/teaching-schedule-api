// src/main/java/com/thuyloiuni/teaching_schedule_api/service/impl/DepartmentServiceImpl.java
package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.DepartmentMapper;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.service.DepartmentService;
import lombok.RequiredArgsConstructor; // Sử dụng Lombok để code gọn hơn
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Tự tạo constructor cho các trường final
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        // Sử dụng phương thức toDtoList đã định nghĩa trong mapper
        return departmentMapper.toDtoList(departmentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Integer id) {
        // Trả về DTO trực tiếp, nếu không thấy sẽ ném exception
        return departmentRepository.findById(id)
                .map(departmentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentByName(String name) {
        return departmentRepository.findByDepartmentName(name)
                .map(departmentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với tên: " + name));
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentRepository.findByDepartmentName(departmentDTO.getDepartmentName()).isPresent()) {
            throw new IllegalArgumentException("Khoa với tên '" + departmentDTO.getDepartmentName() + "' đã tồn tại.");
        }
        Department department = departmentMapper.toEntity(departmentDTO);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Integer id, DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với ID: " + id));

        // Kiểm tra nếu tên thay đổi và tên mới đã tồn tại ở một khoa khác
        if (!existingDepartment.getDepartmentName().equals(departmentDTO.getDepartmentName()) &&
                departmentRepository.findByDepartmentName(departmentDTO.getDepartmentName()).isPresent()) {
            throw new IllegalArgumentException("Khoa với tên '" + departmentDTO.getDepartmentName() + "' đã được sử dụng.");
        }

        existingDepartment.setDepartmentName(departmentDTO.getDepartmentName());
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return departmentMapper.toDto(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy khoa với ID: " + id);
        }
        // Thêm logic kiểm tra ràng buộc trước khi xóa nếu cần
        // Ví dụ: Không cho xóa Khoa nếu vẫn còn Giảng viên
        departmentRepository.deleteById(id);
    }
}
