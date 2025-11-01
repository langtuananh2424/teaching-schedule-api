package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.*;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.AssignmentMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.LecturerMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.StudentClassMapper;
import com.thuyloiuni.teaching_schedule_api.mapper.SubjectMapper;
import com.thuyloiuni.teaching_schedule_api.repository.*;
import com.thuyloiuni.teaching_schedule_api.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentClassRepository studentClassRepository;
    private final LecturerRepository lecturerRepository;
    private final SemesterRepository semesterRepository;

    private final AssignmentMapper assignmentMapper;
    private final SubjectMapper subjectMapper;
    private final LecturerMapper lecturerMapper;
    private final StudentClassMapper studentClassMapper;

    // --- CRUD Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAllAssignments() {
        return assignmentMapper.toDtoList(assignmentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentDTO getAssignmentById(Integer id) {
        return assignmentRepository.findById(id)
                .map(assignmentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAssignmentsByLecturer(Integer lecturerId) {
        if (!lecturerRepository.existsById(lecturerId)) {
            throw new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + lecturerId);
        }
        return assignmentMapper.toDtoList(assignmentRepository.findByLecturer_LecturerId(lecturerId));
    }

    @Override
    @Transactional
    public AssignmentDTO createAssignment(CreateAssignmentDTO createDto) {
        Subject subject = subjectRepository.findById(createDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với ID: " + createDto.getSubjectId()));
        StudentClass studentClass = studentClassRepository.findById(createDto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + createDto.getClassId()));
        Lecturer lecturer = lecturerRepository.findById(createDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + createDto.getLecturerId()));
        Semester semester = semesterRepository.findById(createDto.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy học kỳ với ID: " + createDto.getSemesterId()));

        Assignment newAssignment = new Assignment();
        newAssignment.setSubject(subject);
        newAssignment.setStudentClass(studentClass);
        newAssignment.setLecturer(lecturer);
        newAssignment.setSemester(semester);

        Assignment savedAssignment = assignmentRepository.save(newAssignment);
        return assignmentMapper.toDto(savedAssignment);
    }

    @Override
    @Transactional
    public AssignmentDTO updateAssignment(Integer id, CreateAssignmentDTO updateDto) {
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + id));

        Subject subject = subjectRepository.findById(updateDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với ID: " + updateDto.getSubjectId()));
        StudentClass studentClass = studentClassRepository.findById(updateDto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + updateDto.getClassId()));
        Lecturer lecturer = lecturerRepository.findById(updateDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + updateDto.getLecturerId()));
        Semester semester = semesterRepository.findById(updateDto.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy học kỳ với ID: " + updateDto.getSemesterId()));

        existingAssignment.setSubject(subject);
        existingAssignment.setStudentClass(studentClass);
        existingAssignment.setLecturer(lecturer);
        existingAssignment.setSemester(semester);

        Assignment updatedAssignment = assignmentRepository.save(existingAssignment);
        return assignmentMapper.toDto(updatedAssignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Integer id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy phân công với ID: " + id);
        }
        assignmentRepository.deleteById(id);
    }

    // --- Report Filtering Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsBySemester(Integer semesterId) {
        return assignmentRepository.findSubjectsBySemesterId(semesterId)
                .stream()
                .map(subjectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturersBySemesterAndSubject(Integer semesterId, Integer subjectId) {
        return assignmentRepository.findLecturersBySemesterAndSubject(semesterId, subjectId)
                .stream()
                .map(lecturerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentClassDTO> getClassesBySemesterAndSubjectAndLecturer(Integer semesterId, Integer subjectId, Integer lecturerId) {
        return assignmentRepository.findStudentClassesBySemesterAndSubjectAndLecturer(semesterId, subjectId, lecturerId)
                .stream()
                .map(studentClassMapper::toDto)
                .collect(Collectors.toList());
    }
}
