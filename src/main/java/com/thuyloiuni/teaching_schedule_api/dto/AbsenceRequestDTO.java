package com.thuyloiuni.teaching_schedule_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for representing an absence request with detailed approval statuses.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbsenceRequestDTO {

    // ---- Request Information ----
    private Integer id;
    private final String requestType = "Xin nghỉ dạy";
    private String reason;
    private LocalDateTime createdAt;

    // --- New Approval Statuses ---
    private ApprovalStatus departmentStatus; // Department's approval status
    private ApprovalStatus ctsvStatus;       // CTSV's approval status

    // ---- Lecturer Information ----
    private String lecturerName;

    // ---- Original Session Information ----
    private String subjectName;
    private String className;
    private LocalDate sessionDate;
    private Integer startPeriod;
    private Integer endPeriod;
    private String classroom;

    // ---- Proposed Makeup Session (optional) ----
    private LocalDateTime makeupCreatedAt;
    private LocalDate makeupDate;
    private Integer makeupStartPeriod;
    private Integer makeupEndPeriod;
    private String makeupClassroom;
}
