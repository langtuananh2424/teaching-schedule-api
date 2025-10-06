package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.AssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAssignmentDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Assignment;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import com.thuyloiuni.teaching_schedule_api.entity.Subject;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.AssignmentMapper;
import com.thuyloiuni.teaching_schedule_api.repository.AssignmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.StudentClassRepository;
import com.thuyloiuni.teaching_schedule_api.repository.SubjectRepository;
import com.thuyloiuni.teaching_schedule_api.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentClassRepository studentClassRepository;
    private final LecturerRepository lecturerRepository;
    private final AssignmentMapper assignmentMapper;

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
        // Tìm các đối tượng liên quan
        Subject subject = subjectRepository.findById(createDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với ID: " + createDto.getSubjectId()));
        StudentClass studentClass = studentClassRepository.findById(createDto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + createDto.getClassId()));
        Lecturer lecturer = lecturerRepository.findById(createDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + createDto.getLecturerId()));

        Assignment newAssignment = new Assignment();
        newAssignment.setSubject(subject);
        newAssignment.setStudentClass(studentClass);
        newAssignment.setLecturer(lecturer);
        newAssignment.setTheorySession(createDto.getTheorySession());
        newAssignment.setPracticeSession(createDto.getPracticeSession());

        Assignment savedAssignment = assignmentRepository.save(newAssignment);
        return assignmentMapper.toDto(savedAssignment);
    }

    @Override
    @Transactional
    public AssignmentDTO updateAssignment(Integer id, CreateAssignmentDTO updateDto) {
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + id));

        // Tương tự, tìm các đối tượng liên quan
        Subject subject = subjectRepository.findById(updateDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với ID: " + updateDto.getSubjectId()));
        StudentClass studentClass = studentClassRepository.findById(updateDto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học với ID: " + updateDto.getClassId()));
        Lecturer lecturer = lecturerRepository.findById(updateDto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + updateDto.getLecturerId()));

        // Cập nhật thông tin
        existingAssignment.setSubject(subject);
        existingAssignment.setStudentClass(studentClass);
        existingAssignment.setLecturer(lecturer);
        existingAssignment.setTheorySession(updateDto.getTheorySession());
        existingAssignment.setPracticeSession(updateDto.getPracticeSession());

        Assignment updatedAssignment = assignmentRepository.save(existingAssignment);
        return assignmentMapper.toDto(updatedAssignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Integer id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy phân công với ID: " + id);
        }
        // Thêm logic kiểm tra ràng buộc (ví dụ: không cho xóa nếu đã có lịch học) trước khi xóa
        assignmentRepository.deleteById(id);
    }
}
