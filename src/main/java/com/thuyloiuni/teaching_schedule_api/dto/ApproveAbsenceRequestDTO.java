package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveAbsenceRequestDTO {
    @NotNull(message = "ID người duyệt không được để trống")
    private Integer approverId;

    @NotNull(message = "Trạng thái duyệt không được để trống")
    private ApprovalStatus newStatus;
}
