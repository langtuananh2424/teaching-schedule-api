package com.thuyloiuni.teaching_schedule_api.entity.enums;

/**
 * Represents the approval status for a single level of the approval process (e.g., by Department or by CTSV).
 */
public enum ApprovalStatus {
    PENDING,  // Waiting for review
    APPROVED, // Approved by this level
    REJECTED  // Rejected by this level
}
