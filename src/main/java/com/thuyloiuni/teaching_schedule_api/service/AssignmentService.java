package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.AssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAssignmentDTO;

import java.util.List;

public interface AssignmentService {
    List<AssignmentDTO> getAllAssignments();
    AssignmentDTO getAssignmentById(Integer id);
    List<AssignmentDTO> getAssignmentsByLecturer(Integer lecturerId);
    AssignmentDTO createAssignment(CreateAssignmentDTO createDto);
    AssignmentDTO updateAssignment(Integer id, CreateAssignmentDTO updateDto);
    void deleteAssignment(Integer id);
}
