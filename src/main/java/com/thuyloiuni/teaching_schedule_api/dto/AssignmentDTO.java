package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.Data;

@Data
public class AssignmentDTO {
    private Integer assignmentId;

    // Thông tin Môn học
    private Integer subjectId;
    private String subjectName;

    // Thông tin Lớp học
    private Integer classId;
    private String className;

    // Thông tin Giảng viên
    private Integer lecturerId;
    private String lecturerName;

    private Integer theorySession;
    private Integer practiceSession;
}
