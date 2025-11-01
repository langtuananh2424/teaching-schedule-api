package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import lombok.Data;

/**
 * DTO for updating the approval status of a single level (Department or CTSV).
 */
@Data
public class UpdateApprovalStatusDTO {
    private ApprovalStatus status;
    // Future extensions can include fields like 'rejectionReason' or 'approverComment'.
}
