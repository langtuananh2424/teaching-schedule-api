package com.thuyloiuni.teaching_schedule_api.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.service.DepartmentService;

@Service

public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository, ModelMapper modelMapper) {
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentRepository.findByDepartName(departmentDTO.getDepartmentName()).isPresent()) {
            throw new IllegalArgumentException("Department with name " + departmentDTO.getDepartmentName() + " already exists.");
        }
        Department department = modelMapper.map(departmentDTO, Department.class);
        Department savedDepartment = departmentRepository.save(department);
        return modelMapper.map(savedDepartment, DepartmentDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .map(department -> modelMapper.map(department, DepartmentDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> modelMapper.map(department, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Integer id, DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        if (!existingDepartment.getDepartmentName().equals(departmentDTO.getDepartmentName()) &&
                departmentRepository.findByDepartName(departmentDTO.getDepartmentName()).filter(d -> !d.getDepartmentId().equals(id)).isPresent()) {
            throw new IllegalArgumentException("Department with name " + departmentDTO.getDepartmentName() + " already exists.");
        }

        existingDepartment.setDepartmentName(departmentDTO.getDepartmentName());
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return modelMapper.map(updatedDepartment, DepartmentDTO.class);
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id " + id);
        }
        departmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> getDepartmentByName(String name) {
        return departmentRepository.findByDepartName(name)
                .map(department -> modelMapper.map(department, DepartmentDTO.class));
    }
}
