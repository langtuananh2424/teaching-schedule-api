package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.Data;

@Data
public class StudentClassDTO {
    private Integer classId;
    private String classCode;
    private String className;
    private String semester;
}
