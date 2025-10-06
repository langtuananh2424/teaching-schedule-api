package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MakeupSessionDTO {
    private Integer makeupSessionId;
    private Integer absentSessionId; // ID của buổi học đã nghỉ
    private LocalDateTime makeupDate;
    private Integer makeupStartPeriod;
    private Integer makeupEndPeriod;
    private String makeupClassroom;
    private ApprovalStatus approvalStatus;
    private Integer approverId; // ID người duyệt
    private String approverName; // Tên người duyệt
}
