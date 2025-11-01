package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.AssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.dto.StudentClassDTO;
import com.thuyloiuni.teaching_schedule_api.dto.SubjectDTO;

import java.util.List;

public interface AssignmentService {
    // --- CRUD Methods ---
    List<AssignmentDTO> getAllAssignments();
    AssignmentDTO getAssignmentById(Integer id);
    List<AssignmentDTO> getAssignmentsByLecturer(Integer lecturerId);
    AssignmentDTO createAssignment(CreateAssignmentDTO createDto);
    AssignmentDTO updateAssignment(Integer id, CreateAssignmentDTO updateDto);
    void deleteAssignment(Integer id);

    // --- Report Filtering Methods ---
    List<SubjectDTO> getSubjectsBySemester(Integer semesterId);
    List<LecturerDTO> getLecturersBySemesterAndSubject(Integer semesterId, Integer subjectId);
    List<StudentClassDTO> getClassesBySemesterAndSubjectAndLecturer(Integer semesterId, Integer subjectId, Integer lecturerId);
}
