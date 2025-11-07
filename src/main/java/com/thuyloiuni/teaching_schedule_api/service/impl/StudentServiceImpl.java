package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.StudentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateStudentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import com.thuyloiuni.teaching_schedule_api.entity.Student;
import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.StudentMapper;
import com.thuyloiuni.teaching_schedule_api.repository.ScheduleRepository;
import com.thuyloiuni.teaching_schedule_api.repository.StudentClassRepository;
import com.thuyloiuni.teaching_schedule_api.repository.StudentRepository;
import com.thuyloiuni.teaching_schedule_api.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;
    private final ScheduleRepository scheduleRepository; // Added this repository
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentMapper.toDtoList(studentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Integer id) {
        return studentRepository.findById(id)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByClassId(Integer classId) {
        if (!studentClassRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + classId);
        }
        return studentMapper.toDtoList(studentRepository.findByStudentClass_ClassId(classId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByScheduleId(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi học với ID: " + scheduleId));

        StudentClass studentClass = schedule.getAssignment().getStudentClass();
        if (studentClass == null || studentClass.getStudents() == null) {
            return Collections.emptyList(); // Return empty list if no class or students are associated
        }

        // Convert Set<Student> to List<Student> before passing to the mapper
        return studentMapper.toDtoList(new ArrayList<>(studentClass.getStudents()));
    }

    @Override
    @Transactional
    public StudentDTO createStudent(CreateStudentDTO createStudentDTO) {
        if (studentRepository.existsByStudentCode(createStudentDTO.getStudentCode())) {
            throw new IllegalArgumentException("Mã sinh viên '" + createStudentDTO.getStudentCode() + "' đã tồn tại.");
        }

        StudentClass studentClass = studentClassRepository.findById(createStudentDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + createStudentDTO.getClassId()));

        Student newStudent = studentMapper.fromCreateDtoToEntity(createStudentDTO);
        newStudent.setStudentClass(studentClass);

        Student savedStudent = studentRepository.save(newStudent);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentDTO updateStudent(Integer id, CreateStudentDTO createStudentDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + id));

        StudentClass studentClass = studentClassRepository.findById(createStudentDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + createStudentDTO.getClassId()));

        existingStudent.setStudentCode(createStudentDTO.getStudentCode());
        existingStudent.setFullName(createStudentDTO.getFullName());
        existingStudent.setStudentClass(studentClass);

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}
