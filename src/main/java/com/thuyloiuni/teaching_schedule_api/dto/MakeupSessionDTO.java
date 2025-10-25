package com.thuyloiuni.teaching_schedule_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thuyloiuni.teaching_schedule_api.model.ApprovalStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MakeupSessionDTO {
    private Integer makeupSessionId;
    private Integer absentSessionId; // ID of the original absent session
    private LocalDateTime makeupDate;
    private Integer makeupStartPeriod;
    private Integer makeupEndPeriod;
    private String makeupClassroom;
    private LocalDateTime createdAt;

    // --- New Approval Statuses ---
    private ApprovalStatus departmentStatus;
    private ApprovalStatus ctsvStatus;
}
