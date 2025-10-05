package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.CreateStudentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentDTO;

import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudents();
    StudentDTO getStudentById(Integer id);
    List<StudentDTO> getStudentsByClassId(Integer classId);
    StudentDTO createStudent(CreateStudentDTO createStudentDTO);
    StudentDTO updateStudent(Integer id, CreateStudentDTO createStudentDTO);
    void deleteStudent(Integer id);
}
