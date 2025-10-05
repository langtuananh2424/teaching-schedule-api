package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;

import java.util.List;

public interface StudentClassService {
    List<StudentClassDTO> getAllClasses();
    StudentClassDTO getClassById(Integer id);
    StudentClassDTO createClass(StudentClassDTO studentClassDTO);
    StudentClassDTO updateClass(Integer id, StudentClassDTO studentClassDTO);
    void deleteClass(Integer id);
}

