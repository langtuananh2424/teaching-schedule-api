package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttendanceDTO {
    private Integer attendanceId;
    private Integer sessionId;
    private Integer studentId;
    private String studentCode;
    private String studentFullName;
    private Boolean isPresent;
    private LocalDateTime timestamp;
}