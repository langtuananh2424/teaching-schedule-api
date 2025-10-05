// src/main/java/com/thuyloiuni/teaching_schedule_api/service/impl/DepartmentServiceImpl.java
package com.thuyloiuni.teaching_schedule_api.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.DepartmentMapper; // THÊM IMPORT NÀY
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.service.DepartmentService;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper; // THÊM DÒNG NÀY

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                 DepartmentMapper departmentMapper) { // THÊM THAM SỐ NÀY
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper; // THÊM DÒNG NÀY
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentRepository.findByDepartmentName(departmentDTO.getDepartmentName()).isPresent()) {
            throw new IllegalArgumentException("Department with name " + departmentDTO.getDepartmentName() + " already exists.");
        }
        // Sử dụng departmentMapper để chuyển DTO sang Entity
        Department department = departmentMapper.departmentDTOToDepartment(departmentDTO);
        Department savedDepartment = departmentRepository.save(department);
        // Sử dụng departmentMapper để chuyển Entity sang DTO
        return departmentMapper.departmentToDepartmentDTO(savedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .map(departmentMapper::departmentToDepartmentDTO); // Sử dụng method reference
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::departmentToDepartmentDTO) // Sử dụng method reference
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Integer id, DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        // Kiểm tra nếu tên thay đổi và tên mới đã tồn tại cho department khác
        if (!existingDepartment.getDepartmentName().equals(departmentDTO.getDepartmentName()) &&
            departmentRepository.findByDepartmentName(departmentDTO.getDepartmentName())
                                .filter(d -> !d.getDepartmentId().equals(id)) // Chỉ kiểm tra các department khác
                                .isPresent()) {
            throw new IllegalArgumentException("Department with name " + departmentDTO.getDepartmentName() + " already exists.");
        }

        // Chỉ cập nhật tên. Không cần map toàn bộ đối tượng DTO vào existingDepartment
        // nếu chỉ có một vài trường thay đổi và DTO đơn giản.
        // Tuy nhiên, nếu DTO có nhiều trường hơn và bạn muốn map chúng, có thể làm như sau:
        // Department mappedUpdates = departmentMapper.departmentDTOToDepartment(departmentDTO);
        // existingDepartment.setDepartmentName(mappedUpdates.getDepartmentName());
        // ... set các trường khác nếu có ...

        // Cách đơn giản và rõ ràng hơn cho trường hợp này:
        existingDepartment.setDepartmentName(departmentDTO.getDepartmentName());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return departmentMapper.departmentToDepartmentDTO(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id " + id);
        }
        // Trước khi xóa, cân nhắc kiểm tra xem department có lecturers hoặc subjects nào không
        // Department entity = departmentRepository.findById(id).get();
        // if (entity.getLecturers() != null && !entity.getLecturers().isEmpty()) {
        //     throw new IllegalStateException("Cannot delete department with associated lecturers.");
        // }
        // if (entity.getSubjects() != null && !entity.getSubjects().isEmpty()) {
        //     throw new IllegalStateException("Cannot delete department with associated subjects.");
        // }
        departmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> getDepartmentByName(String name) {
        return departmentRepository.findByDepartmentName(name)
                .map(departmentMapper::departmentToDepartmentDTO); // Sử dụng method reference
    }
}
