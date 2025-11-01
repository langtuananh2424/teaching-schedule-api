package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.Data;

@Data
public class AssignmentDTO {
    private Integer assignmentId;
    private SubjectDTO subject;
    private StudentClassDTO studentClass;
    private LecturerDTO lecturer;
    private SemesterDTO semester;
}
