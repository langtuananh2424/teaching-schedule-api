package com.thuyloiuni.teaching_schedule_api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thuyloiuni.teaching_schedule_api.dto.DepartmentDTO;
import com.thuyloiuni.teaching_schedule_api.service.DepartmentService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    //API để tạo mới một Department
    //POST http://localhost:8080/api/departments
    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED); // Trả về 201 Created
    }

    //API để lấy tất cả Departments
    //GET http://localhost:8080/api/departments
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return new ResponseEntity<>(departments, HttpStatus.OK); // Trả về 200 OK
    }

    //API để lấy Department theo ID
    //GET http://localhost:8080/api/departments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Integer id) {
        Optional<DepartmentDTO> departmentDTO = departmentService.getDepartmentById(id);
        return departmentDTO
            .map(ResponseEntity::ok) // Trả về 200 OK nếu tìm thấy
            .orElse(ResponseEntity.notFound().build()); // Trả về 404 Not Found nếu không tìm thấy
    }
    
    //API để lấy một Department theo tên
    //GET http://localhost:8080/api/departments/name/{name}
    @GetMapping("/name/{name}")
    public ResponseEntity<DepartmentDTO> getDepartmentByName(@PathVariable String name) {
        Optional<DepartmentDTO> departmentDTO = departmentService.getDepartmentByName(name);
        return departmentDTO
            .map(ResponseEntity::ok) // Trả về 200 OK nếu tìm thấy
            .orElse(ResponseEntity.notFound().build()); // Trả về 404 Not Found nếu không tìm thấy
    }
    
    // API để cập nhật một Department hiện có
    // PUT http://localhost:8080/api/departments/{id}
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable Integer id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        // Service sẽ ném ResourceNotFoundException nếu không tìm thấy, 
        // GlobalExceptionHandler sẽ xử lý thành 404
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updatedDepartment); // Trả về 200 OK với DTO đã cập nhật
    }

    // API để xóa một Department
    // DELETE http://localhost:8080/api/departments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        // Service sẽ ném ResourceNotFoundException nếu không tìm thấy
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content (xóa thành công)
    }
    
    
}
