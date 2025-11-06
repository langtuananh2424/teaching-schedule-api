package com.thuyloiuni.teaching_schedule_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbsenceRequestDTO {
    // --- Absence Request Info ---
    private Integer absenceRequestId;
    private String reason;
    private LocalDateTime createdAt;

    // --- Lecturer and Class Info ---
    private String lecturerName;
    private String subjectName;
    private String className;

    // --- Original Session Info ---
    private LocalDate sessionDate;
    private Integer startPeriod;
    private Integer endPeriod;
    private String classroom;

    // --- Approval Statuses ---
    private ApprovalStatus managerStatus;
    private ApprovalStatus academicAffairsStatus;

    // --- Makeup Session Info (if exists) ---
    private LocalDateTime makeupCreatedAt;
    private LocalDate makeupDate;
    private Integer makeupStartPeriod;
    private Integer makeupEndPeriod;
    private String makeupClassroom;
}
