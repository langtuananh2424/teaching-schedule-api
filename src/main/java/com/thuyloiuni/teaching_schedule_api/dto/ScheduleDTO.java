package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Integer sessionId;
    private Integer assignmentId;

    // Thông tin bổ sung để hiển thị cho dễ
    private String subjectName;
    private String lecturerName;
    private String className;

    private LocalDateTime sessionDate;
    private Integer lessonOrder;
    private Integer startPeriod;
    private Integer endPeriod;
    private String classroom;
    private String content;
    private ScheduleStatus status;
    private String notes;
}
