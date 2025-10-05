package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AbsenceRequestDTO {
    private Integer requestId;
    private Integer sessionId; // Lấy ID từ Schedule
    private Integer lecturerId; // ID người tạo đơn
    private String lecturerName; // Tên người tạo đơn
    private String reason;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private Integer approverId; // ID người duyệt
    private String approverName; // Tên người duyệt
}
