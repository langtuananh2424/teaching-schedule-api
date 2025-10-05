package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Integer studentId;
    private String studentCode;
    private String fullName;

    private Integer classId;
    private String className;
}
